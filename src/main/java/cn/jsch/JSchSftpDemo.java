package cn.jsch;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.Properties;

/**
 * @BelongsProject: MavenProjectDemo
 * @BelongsPackage: cn.jsch
 * @Author: lhb
 * @CreateTime: 2019-10-18 12:00
 * @Description: sftp通道
 */
public class JSchSftpDemo {
    public static void main(String[] args) throws IOException {
        System.out.println(File.separatorChar);
//        FTPClient ftpClient = new FTPClient();
//        ftpClient.connect("vm.docker",22);
//        boolean b = ftpClient.login("docker","docker");
//        System.out.println("连接成功:"+b);
        ChannelSftp sftp = null;
        Channel channel = null;
        Session sshSession = null;

        String username = "root";
        String password = "123456";
        String host = "192.168.16.137";
        int port = 22;

        try {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            System.out.println("连接成功");
            channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            InputStream inputStream = sftp.get("/root/logs/test.txt");
            //读取
            InputStreamReader isr = new InputStreamReader(inputStream);

            BufferedReader bufr = new BufferedReader(isr);
            String line;
            while ((line = bufr.readLine()) != null) {
                System.out.println(line);
            }
            isr.close();
//            Vector vector = sftp.ls("/root/logs/test.txt");
//            for (Object item : vector) {
//                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) item;
//                System.out.println(entry.getFilename());
//            }

        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        } finally {
            closeChannel(sftp);
            closeChannel(channel);
            closeSession(sshSession);
        }

    }

    private static void closeChannel(Channel channel) {
        if (channel != null) {
            if (channel.isConnected()) {
                channel.disconnect();
            }
        }
    }

    private static void closeSession(Session session) {
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }
}
