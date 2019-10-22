package cn.jsch;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @BelongsProject: MavenProjectDemo
 * @BelongsPackage: cn.jsch
 * @Author: lhb
 * @CreateTime: 2019-10-18 11:16
 * @Description: JSchExecDemo exec通道
 */
public class JSchExecDemo {
    // 设置编码格式
    private String charset = "UTF-8";
    // 用户名
    private String user;
    // 登录密码
    private String passwd;
    // 主机IP
    private String host;
    private JSch jsch;
    private Session session;

    /**
     * @param user   用户名
     * @param passwd 密码
     * @param host   主机IP
     */
    public JSchExecDemo(String user, String passwd, String host) {
        this.user = user;
        this.passwd = passwd;
        this.host = host;
    }

    /**
     * 连接到指定的IP
     *
     * @throws JSchException
     */
    public void connect() throws JSchException {
        jsch = new JSch();
        session = jsch.getSession(user, host, 22);
        session.setPassword(passwd);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
    }

    /**
     * 执行相关的命令
     */
    public void execCmd() {

        //String command = "ls ;rm -f aa.log; ls ";
        //String command = "tar -czf aa.tar.gz aa.log";
        //String command = "tar -zxvf aa.tar.gz";
        //String command = "tar -zxvf aa.tar.gz";
        //String command = "/root/test.sh";
        //String command = "reboot";
        //String command = "echo abc >> xx.txt";
        String command = "ls";

        //String command = "echo \"always\" >/sys/kernel/mm/transparent_hugepage/enabled ";
        BufferedReader reader = null;
        Channel channel = null;

        try {
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            channel.connect();
            InputStream in = channel.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in, Charset.forName(charset)));
            String buf;
            while ((buf = reader.readLine()) != null) {
                System.out.println(buf);
            }
        } catch (IOException | JSchException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (channel != null) {
                channel.disconnect();
            }
            session.disconnect();
        }
    }

    public static void main(String[] args) throws Exception {
        String user = "root";
        String passwd = "123456";
        String host = "192.168.16.137";

        JSchExecDemo demo = new JSchExecDemo(user, passwd, host);
        demo.connect();
        demo.execCmd();


    }

}
