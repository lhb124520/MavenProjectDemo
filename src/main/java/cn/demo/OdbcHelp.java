package cn.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;

import static cn.demo.MyPath.getProperties;

/**
 * 描述:
 *
 * @author : lhb
 * @date : 2020-06-09 16:21
 */

public class OdbcHelp {
    private static Connection conn;
    private static PreparedStatement ps;
    private static ResultSet rs;
    private static String odbc_driver;
    private static String odbc_url;
    private static String username;
    private static String password;
    private static Logger logger = LoggerFactory.getLogger(OdbcHelp.class);


    public static void main(String[] args) {
        getConnection(args);
        // 测试
        String sql = "select * from dba_users";
        ResultSet rst = executeQuery(sql, null);
        try {
            System.out.println("ODBC查询所有的用户名：");
            logger.info("ODBC查询所有的用户名：");
            while (rst.next()) {
                System.out.println(rst.getString("username"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } finally {
            close(getConn(), getPs(), rst);
        }
    }


    /**
     * 初始化变量
     *  1、传参：java -Dodbc_driver=oracle.jdbc.driver.OracleDriver -Dodbc_url=jdbc:oracle:thin:@172.168.201.81:1521:oracle19  -Dodbc_username=c##gzsendi -Dodbc_password=gzsendi -jar MavenProjectDemo.jar
     *  2、读取配置文件 java -jar MavenProjectDemo.jar
     *
     * @param args 参数
     */
    public static void getConnection(String[] args) {
        for (String arg : args) {
            logger.info("传入的参数：" + arg);
            System.out.println("传入的参数：" + arg);
        }

        try {
            //从参数传入，如果不传参，就从配置文件获取参数dbinfor.properties
            Properties properties = System.getProperties();
            odbc_driver = properties.getProperty("odbc_driver");
            if (odbc_driver == null) {
                //dbinfor.properties在工程路径下面
                odbc_driver = getProperties("dbinfor.properties", "odbc_driver");
            }

            odbc_url = properties.getProperty("odbc_url");
            if (odbc_url == null) {
                //dbinfor.properties在工程路径下面
                odbc_url = getProperties("dbinfor.properties", "odbc_url");
            }

            username = properties.getProperty("odbc_username");
            if (username == null) {
                //dbinfor.properties在工程路径下面
                username = getProperties("dbinfor.properties", "odbc_username");
            }

            password = properties.getProperty("odbc_password");
            if (password == null) {
                //dbinfor.properties在工程路径下面
                password = getProperties("dbinfor.properties", "odbc_password");
            }

            try {
                Class.forName(odbc_driver);
                System.out.println("odbc驱动连接成功");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    //增删改方法
    public static void executeUpdate(String sql, String[] parameters) {
        try {
            conn = DriverManager.getConnection(odbc_url, username, password);
            ps = conn.prepareStatement(sql);
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    ps.setString(i + 1, parameters[i]);
                }
            }
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            close(conn, ps, rs);
        }

    }

    //查询方法
    public static ResultSet executeQuery(String sql, String[] parameters) {
        try {
            conn = DriverManager.getConnection(odbc_url, username, password);
            ps = conn.prepareStatement(sql);
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    ps.setString(i + 1, parameters[i]);
                }
            }
            //执行查询
            rs = ps.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            //关闭资源
            //SQLHelper.close(conn, ps, rs);
        }
        return rs;
    }

    //关闭资源
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            rs = null;
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            stmt = null;
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            conn = null;
        }
    }

    public static Connection getConn() {
        return conn;
    }

    public static PreparedStatement getPs() {
        return ps;
    }


}
