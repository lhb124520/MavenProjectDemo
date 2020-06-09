package cn.demo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @BelongsProject: demo
 * @BelongsPackage: com.example.demo.service.Impl
 * @Author: lhb
 * @CreateTime: 2019-09-28 15:34
 * @Description:
 */

public class MyPath {

    /**
     * 获取项目所在路径(包括jar)
     *
     * @return 项目所在路径
     */
    public static String getProjectPath() {

        java.net.URL url = MyPath.class.getProtectionDomain().getCodeSource()
                .getLocation();
        String filePath = null;
        try {
            filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar")) {
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }
        java.io.File file = new java.io.File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }

    /**
     * 获取项目所在路径
     *
     * @return
     */
    public static String getRealPath() {
        String realPath = MyPath.class.getClassLoader().getResource("")
                .getFile();
        java.io.File file = new java.io.File(realPath);
        realPath = file.getAbsolutePath();
        try {
            realPath = java.net.URLDecoder.decode(realPath, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return realPath;
    }

    public static String getAppPath(Class<?> cls) {
        // 检查用户传入的参数是否为空
        if (cls == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        ClassLoader loader = cls.getClassLoader();
        // 获得类的全名，包括包名
        String clsName = cls.getName();
        // 此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
        if (clsName.startsWith("java.") || clsName.startsWith("javax.")) {
            throw new IllegalArgumentException("不要传送系统类！");
        }
        // 将类的class文件全名改为路径形式
        String clsPath = clsName.replace(".", "/") + ".class";

        // 调用ClassLoader的getResource方法，传入包含路径信息的类文件名
        java.net.URL url = loader.getResource(clsPath);
        // 从URL对象中获取路径信息
        String realPath = url.getPath();
        // 去掉路径信息中的协议名"file:"
        int pos = realPath.indexOf("file:");
        if (pos > -1) {
            realPath = realPath.substring(pos + 5);
        }
        // 去掉路径信息最后包含类文件信息的部分，得到类所在的路径
        pos = realPath.indexOf(clsPath);
        realPath = realPath.substring(0, pos - 1);
        // 如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
        if (realPath.endsWith("!")) {
            realPath = realPath.substring(0, realPath.lastIndexOf("/"));
        }
        java.io.File file = new java.io.File(realPath);
        realPath = file.getAbsolutePath();

        try {
            realPath = java.net.URLDecoder.decode(realPath, "utf-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return realPath;
    }// getAppPath定义结束



    public static String getProperties(String fileName, String property) {
        Properties props = new Properties();
        InputStream is;
        String filePath =  getProjectPath()+"/"+fileName;

        try {
            is = new FileInputStream(filePath);
            props.load(is);
            return props.getProperty(property);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(getRealPath());
        System.out.println(getProjectPath());
        System.out.println(getAppPath(MyPath.class));
        System.out.println(getProperties("application.yml","server2"));
//        InputStream inputStream = MyPath.class.getClassLoader().getResourceAsStream("oraclesql.properties");
//        String path = MyPath.class.getClassLoader().getResource("oraclesql.properties").getPath();
//        System.out.println("oraclesql.properties路径为："+path);
//        Properties props = new Properties();
//        try {
//            props.load(inputStream);
//            String loggerName = props.getProperty("server2");
//            System.out.println(loggerName);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

}

