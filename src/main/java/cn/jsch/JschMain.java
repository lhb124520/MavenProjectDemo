package cn.jsch;

import com.jcraft.jsch.JSchException;

/**
 * 描述: 项目启动类
 *
 * @author : lhb
 * @date : 2019-10-21 16:18
 */
public class JschMain {
    public static void main(String[] args) throws JSchException {
        String user = "root";
        String passwd = "123456";
        String host = "192.168.16.137";
        String command = "sh sendi_zombie_count.sh";
        int port = 22;
        ExecSessionPool.init(host, port, user, passwd);

        String result = ExecSessionPool.getExecResult(command);
        System.out.println(result);
        ExecSessionPool.close();
    }


}
