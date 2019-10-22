package cn.jsch;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static cn.jsch.ExecSessionPool.getExecResult;

/**
 * Created by wubingtao on 2018/4/24.
 * todo 添加本地服务器连接缓存池
 */
public class SFTPSessionPool {

    private static final Logger logger = LoggerFactory.getLogger(SFTPSessionPool.class);

    private static final JSch jsch = new JSch();
    /**
     * 线程绑定Channel
     * 采用初始化值获取Channel时
     * 复用Session
     */
    private static final ThreadLocal<List<ChannelSftp>> SFTP_CHANNEL_COMBINE = new ThreadLocal<>();
    /**
     * Session池及复用计数
     */
    private static final Map<Session, Integer> SESSION_COUNT = new ConcurrentHashMap<>();
    private static final List<Session> SESSION_POOL = new ArrayList<>();
    /**
     * 带参数获取Channel时绑定池
     */
    private static final ThreadLocal<Session> OTHER_IP_SESSION = new ThreadLocal<>();
    /**
     * 主要开销在于Session.connect()
     * 绑定线程与ChannelSftp
     * 经测试
     * 一个Session复用10个Channel(极限11个,再往上无法连接Channel)
     */
    private static final int REUSE_TIMES = 10;

    /**
     * 文件上传保存采用sftp远程保存时使用
     */
    private static String IP = null;
    private static Integer PORT = null;
    private static String USERNAME = null;
    private static String PASSWORD = null;
    private static String ROOTDIRECTORY = null;

    public static void main(String[] args) throws JSchException, SftpException {
        String user = "root";
        String passwd = "123456";
        String host = "192.168.16.137";
        String command = "ls";
        int port = 22;
        init(host, port, user, passwd,null);
        for (int i = 0; i < 20; i++) {
            ChannelSftp channelSftp = getSftpChannel();
            System.out.println("----------------" + i + "-------------------");
        }

    }

    /**
     * 根据初始化参数,获取一个SftpChannel对象,绑定到当前线程
     */
    public static ChannelSftp getSftpChannel() throws JSchException, SftpException {
        if (Objects.isNull(IP)) {
            throw new JSchException("ip地址为空");
        }
        ChannelSftp sftp;
        Session s = getSession();
        sftp = (ChannelSftp) s.openChannel("sftp");
        //线程绑定Channel
        List<ChannelSftp> channelSftpList = SFTP_CHANNEL_COMBINE.get();
        if (channelSftpList == null) {
            channelSftpList = new ArrayList<>();
        }
        SFTP_CHANNEL_COMBINE.set(channelSftpList);
        channelSftpList.add(sftp);
        sftp.connect();
//        TODO FileHandleUtils.cdDirOnSftp(sftp, workRoot);
//        FileHandleUtils.cdDirOnSftp(sftp, workRoot);
        return sftp;
    }

    public static ChannelExec getExecChannel() throws JSchException {
        if (Objects.isNull(IP)) {
            throw new JSchException("ip地址为空");
        }
        return (ChannelExec) getSession().openChannel("exec");
    }

    private static Session getSession() throws JSchException {
        Session session = null;
        for (Session s : SESSION_POOL) {
            synchronized (SESSION_COUNT) {
                Integer i = SESSION_COUNT.get(s);
                if (i < REUSE_TIMES) {
                    session = s;
                    SESSION_COUNT.put(s, ++i);
                    break;
                }
            }
        }
        if (session == null) {
            session = connectSession();
            SESSION_POOL.add(session);
            SESSION_COUNT.put(session, 1);
        }
        return session;
    }

    private static Session connectSession() throws JSchException {
        Session session = jsch.getSession(USERNAME, IP, PORT);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(PASSWORD);
        session.connect(3000);
        return session;
    }

    /**
     * 释放线程绑定的SftpChannel对象
     * e.g.线程绑定释放,但对象并无法释放,故会造成巅峰时刻用户连接造成的Session积压无法释放,可考虑根据情况释放无引用Session
     */
    public static void freeMyFtpClient() {
        Session s1 = OTHER_IP_SESSION.get();
        if (s1 != null) {
            s1.disconnect();
        }
        List<ChannelSftp> sftpList = SFTP_CHANNEL_COMBINE.get();
        if (sftpList != null) {
            for (ChannelSftp sftp : sftpList) {
                if (sftp != null) {
                    try {
                        Session s2 = sftp.getSession();
                        sftp.disconnect();
                        if (s2 != null) {
                            synchronized (SESSION_COUNT) {
                                Integer i = SESSION_COUNT.get(s2);
                                SESSION_COUNT.put(s2, --i);
                            }
                        }
                    } catch (JSchException e) {
                        logger.error("释放Channel错误 -> ", e);
                    }
                }
            }
            sftpList.clear();
        }
    }

    /**
     * 根据给定参数,获取一个SftpChannel对象,绑定到当前线程
     */
    public static ChannelSftp getSftpChannel(String ip, Integer port, String username, String password, String workRoot) throws JSchException, SftpException {
        if (Objects.isNull(ip) || Objects.isNull(username)) {
            throw new JSchException("ip和用户名不能为空");
        }
        if (Objects.isNull(port)) {
            port = 22;
        }
        if (Objects.isNull(workRoot)) {
            workRoot = "";
        }
        if (Objects.isNull(password)) {
            password = "";
        }

        Session session = jsch.getSession(username, ip, port);
        OTHER_IP_SESSION.set(session);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(password);
        session.connect(15000);

        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
//        TODO FileHandleUtils.cdDirOnSftp(sftp, workRoot);
//        FileHandleUtils.cdDirOnSftp(sftp, workRoot);
        return sftp;
    }


    /**
     * 初始化SFTP会话池
     * 用户名与ip不能为空
     */
    public static void init(String ip, Integer port, String username, String password, String rootDirectory) throws JSchException, SftpException {
        if (Objects.isNull(ip) || Objects.isNull(username)) {
            throw new JSchException("ip和用户名不能为空");
        }
        if (Objects.isNull(rootDirectory)) {
            rootDirectory = "/";
        }
        if (password == null) {
            password = "";
        }

        Session session = jsch.getSession(username, ip, port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(password);
        session.connect(3000);
        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
//TODO FileHandleUtils.cdDirOnSftp(sftp, rootDirectory);
//FileHandleUtils.cdDirOnSftp(sftp, rootDirectory);
        sftp.disconnect();
        session.disconnect();

        IP = ip;
        PORT = port;
        USERNAME = username;
        PASSWORD = password;
        ROOTDIRECTORY = rootDirectory;
    }

}
