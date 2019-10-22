package cn.demo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @BelongsProject: MavenProjectDemo
 * @BelongsPackage: cn.demo
 * @Author: lhb
 * @CreateTime: 2019-10-10 14:12
 * @Description:
 */
public class Main {
    private static IUser iUser;

    private static void getProperty(String fileName) {
        Properties prop = new Properties();
        //需要外部属性配置文件的路径
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(fileName);
            prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("获取资源路径报错，错误信息：" + e.getMessage());
            return;
        }

        String driverClass = prop.getProperty("driverClass");
        String url = prop.getProperty("url");
        String username = prop.getProperty("user");
        String password = prop.getProperty("password");
        System.out.println(driverClass + "==" + url + "==" + username + "==" + password);
    }

    /**
     * 打成jar包 输入命令：java -jar MavenProjectDemo.jar /root/src/application.properties
     * @param args 配置文件路径
     */
    public static void main(String[] args) {
        UserB userB =new UserB();
        UserC userC =new UserC();
        System.out.println(userB .getAge());
        System.out.println(userC .getAge());
//        //路径为：/root/src/application.properties
//        if (args.length <= 0) {
//            System.out.println("请指定配置文件路径");
//            return;
//        }
//        System.out.println("输出路径："+args[0]);
//        getProperty(args[0]);
    }
}
