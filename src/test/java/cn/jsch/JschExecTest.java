package cn.jsch;

import com.jcraft.jsch.JSchException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 描述: jsch测试
 *
 * @author : lhb
 * @date : 2019-10-22 11:28
 */
public class JschExecTest {
    private String command = "sh sendi_zombie_count.sh";

    /**
     * 初始化参数
     */
    @BeforeAll
    static void init() throws JSchException {
        String user = "root";
        String passwd = "123456";
        String host = "192.168.16.137";
        int port = 22;
        ExecSessionPool.init(host, port, user, passwd);
    }

    /**
     * 测试使用jsch连接ssh是否有问题
     */
    @Test
    void testJschExec() throws JSchException {
        String result = ExecSessionPool.getExecResult(command);
        System.out.println(result);
        ExecSessionPool.close();
    }

    /**
     * 测试多线程下连接ssh是否有问题
     */
    @Test
    void testJschThread() {
        for (int i = 1; i <= 10; i++) {
            Thread thread = new Thread(() -> {
                try {
                    String result = ExecSessionPool.getExecResult(command);
                    System.out.println(result);
                    System.out.println("-----------------------------------");
                } catch (JSchException e) {
                    e.printStackTrace();
                }
            });
            thread.start();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ExecSessionPool.close();
    }

    /**
     * 测试多线程池下连接ssh是否有问题
     */
    @Test
    void testJschThreadPool() {
        // 创建线程池
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int k = 1; k < 10; k++) {
            cachedThreadPool.submit(() -> {
                String result;
                try {
                    result = ExecSessionPool.getExecResult(command);
                    System.out.println(result);
                    System.out.println("-----------------------------------");
                } catch (JSchException e) {
                    e.printStackTrace();
                }
            });
        }
        cachedThreadPool.shutdown();
        while (true) {
            if (cachedThreadPool.isTerminated()) {
                ExecSessionPool.close();
                break;
            }
        }
    }
}
