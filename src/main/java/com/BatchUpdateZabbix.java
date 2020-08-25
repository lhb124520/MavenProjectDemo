package com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 批量修改zabbix server的数据库的参数
 *
 * @author : lhb
 * @date : 2020-08-21 16:58
 */
public class BatchUpdateZabbix {
    private static Logger log = LoggerFactory.getLogger(BatchUpdateZabbix.class);
    //JDBC驱动名
    private static String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    //数据库的用户名与密码
    private static String USER = "sendi";
    private static String PASS = "sendi@1234";
    private static String CPU = "";
    private static String MEM = "";

    public static void main(String[] args) throws IOException {

        //获取ip列表
        String filePath = args[0];
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("错误:文件{}不存在", filePath);
            System.exit(-1);
        }
        if (!file.canRead()) {
            log.error("错误:无法读取文件{}", filePath);
            System.exit(-1);
        }

        List<String> zabbixServers = getFile(file);

        CPU = getStringFromList(args[1]);
        MEM = getStringFromList(args[2]);

        if (zabbixServers.isEmpty() || "".equalsIgnoreCase(CPU) || "".equalsIgnoreCase(MEM)) {
            log.warn("没有有效的连接信息, 退出");
            return;
        }

        zabbixServers.forEach(BatchUpdateZabbix::optDb);
    }

    private static String getStringFromList(String memPath) throws IOException {
        File memFile = new File(memPath);
        List<String> memList = getFile(memFile);
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : memList) {
            stringBuilder.append(s);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * 操作数据库
     */
    private static void optDb(String ip) {
        Connection connection;
        PreparedStatement statementCpu;
        PreparedStatement statementMem;
        try {
            //注册JDBC驱动
            Class.forName(JDBC_DRIVER);

            //用文件的ip替换
            String jdbcUrl = "jdbc:mysql://ip:3306/zabbix?useSSL=false&serverTimezone=UTC";
            jdbcUrl = jdbcUrl.replace("ip", ip);
            //数据库的连接：通过DriverManager类的getConnection方法，传入三个参数：数据库URL、用户名、用户密码，实例化connection对象
            connection = DriverManager.getConnection(jdbcUrl, USER, PASS);

            //实例化statement对象
            statementCpu = connection.prepareStatement("UPDATE `items` SET params = ? WHERE `name` = 'CPU使用率（SSH）'");
            statementCpu.setString(1,CPU);
            //定义数据库查询语句：查询aa表中的name、sex两列数据
//            String sql = "SELECT username FROM user";
//            String sqlCpu = "UPDATE `items` SET params = '" + CPU + "' WHERE `name` = 'CPU使用率（SSH）'";
            //执行查询语句
            boolean executeCpu = statementCpu.execute();
//            log.info("是否执行cpu成功：" + executeCpu);

//            String sqlMem = "UPDATE `items` SET params = '" + MEM + "' WHERE `name` = '内存占用率（SSH）'";
            //执行查询语句
            statementMem = connection.prepareStatement("UPDATE `items` SET params = ? WHERE `name` = '内存占用率（SSH）'");
            statementMem.setString(1,MEM);
//            boolean executeMem = statementMem.execute();
//            log.info("是否执行内存成功：" + executeMem);
//            ResultSet resultSet = statement.executeQuery(sql);
//
//            //展开查询到的数据
//            while (resultSet.next()) {
//
//                //这里getString()方法中的参数对应的是数据库表中的列名
//                String username = resultSet.getString("username");
//
//                //输出数据
//                System.out.println("名字:" + username);
//            }

            //依次关闭对象
//            resultSet.close();
            statementMem.close();
            statementCpu.close();
            connection.close();
            log.info("执行sql成功");
        } catch (ClassNotFoundException | SQLException e) {
            log.error(ip+"执行失败");
            e.printStackTrace();
        }
    }

    private static List<String> getFile(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        String line;
        List<String> inputStr = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }
            inputStr.add(line);
        }
        bufferedReader.close();
        log.info("读取输入文件完成");
        return inputStr;
    }
}
