package cn.jsch;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述: 添加执行ssh脚本本地服务器连接缓存池
 *
 * @author : lhb
 * @date : 2019-10-12 14:19
 */
public class ExecSessionPool {

    private static final Logger logger = LoggerFactory.getLogger(ExecSessionPool.class);

    private static final JSch JSCH = new JSch();

    /**
     * Session池及复用计数
     */
    private static final Map<Session, Integer> SESSION_COUNT = new ConcurrentHashMap<>();
    private static final List<Session> SESSION_POOL = new ArrayList<>();

    /**
     * 文件上传保存采用sftp远程保存时使用
     */
    private static String IP = null;
    private static int PORT = 22;
    private static String USERNAME = null;
    private static String PASSWORD = null;

    /**
     * 初始化SFTP会话池
     * 用户名与ip不能为空
     */
    public static void init(String ip, int port, String username, String password) throws JSchException {
        if (Objects.isNull(ip) || Objects.isNull(username)) {
            throw new JSchException("ip和用户名不能为空");
        }
        if (password == null) {
            password = "";
        }

        IP = ip;
        PORT = port;
        USERNAME = username;
        PASSWORD = password;
    }

    /**
     * session连接池
     *
     * @return
     * @throws JSchException
     */
    private static Session getSession() throws JSchException {
        Session session = null;
        for (Session s : SESSION_POOL) {
            synchronized (SESSION_COUNT) {
                Integer i = SESSION_COUNT.get(s);
                session = s;
                SESSION_COUNT.put(s, ++i);
            }
        }
        if (session == null) {
            session = connectSession();
            SESSION_POOL.add(session);
            SESSION_COUNT.put(session, 1);
        }
        return session;
    }

    /**
     * 根据初始化的参数从连接池获取session连接
     *
     * @return Session实例
     */
    private static Session connectSession() throws JSchException {
        Session session = JSCH.getSession(USERNAME, IP, PORT);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(PASSWORD);
        session.connect(3000);
        return session;
    }

    /**
     * 根据初始化参数,获取一个ssh脚本执行的字符串结果
     *
     * @param command linux命令
     * @return ssh脚本执行的字符串结果
     * @throws JSchException 调用jsch框架失败
     */
    public static String getExecResult(String command) throws JSchException {
        ChannelExec channel = (ChannelExec) getSession().openChannel("exec");
        channel.setCommand(command);
        channel.connect();

        //使用try-with-resources优雅关闭IO资源
        try (InputStream inputStream = channel.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            StringBuilder result = new StringBuilder();
            String buf;
            while ((buf = bufferedReader.readLine()) != null) {
                result.append(buf).append("\n");
            }
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new JSchException("调用jsch框架失败：", e);
        } finally {
            channel.disconnect();
        }
    }

    /**
     * 根据初始化的参数从连接池获取ChannelExec
     *
     * @return Exec通道
     */
    public static ChannelExec getExecChannel() throws JSchException {
        if (Objects.isNull(IP)) {
            throw new JSchException("ip地址为空");
        }
        return (ChannelExec) getSession().openChannel("exec");
    }

    /**
     * 关闭session连接
     */
    public static void close() {
        for (Session session : SESSION_POOL) {
            synchronized (SESSION_COUNT) {
                if (session != null) {
                    session.disconnect();
                }
            }
        }
        SESSION_POOL.clear();
        SESSION_COUNT.clear();
    }
}
