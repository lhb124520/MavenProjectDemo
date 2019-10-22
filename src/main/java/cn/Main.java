package cn;

import cn.jsch.ExecSessionPool;
import com.jcraft.jsch.JSchException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 描述: 项目启动类
 *
 * @author : lhb
 * @date : 2019-10-21 16:18
 */
public class Main {
    public static void main(String[] args) throws JSchException {
        String user = "root";
        String passwd = "123456";
        String host = "192.168.16.137";
        String command = "ls";
        int port = 22;
        ExecSessionPool.init(host, port, user, passwd);
        for (int i = 0; i < 20; i++) {
            String result = ExecSessionPool.getExecResult(command);
            System.out.println(result);
            System.out.println("----------------" + i + "-------------------");
        }
        ExecSessionPool.close();
//        Instant instant;
//        Date date;
//        LocalDateTime localDateTime;
//        Calendar calendar;
//        ArrayList<String> arrayList = new ArrayList<>();
//        List<String> list = arrayList.subList(1,2);
    }

    private static final ThreadLocal<DateFormat> dateFormatThreadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

}
