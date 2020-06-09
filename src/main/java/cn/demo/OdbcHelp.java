package cn.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

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
        // 测试
        String sql = "select * from dba_users";
        ResultSet rst = executeQuery(sql, null);
        try {
            System.out.println("ODBC查询所有的用户名：");
            logger.info("ODBC查询所有的用户名：");
            while (rst.next()) {
                System.out.println(rst.getString("oracle_username"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } finally {
            close(getConn(), getPs(), rst);
        }
    }


    static {
        try {
            Map<String, String> map = get();
            odbc_driver = map.get("odbc_driver");
            odbc_url = map.get("odbc_url");
            username = map.get("oracle_username");
            password = map.get("password");

            Class.forName(odbc_driver);
            System.out.println("odbc驱动连接成功");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    //初始化变量
    public static Map get() {
        Map<String, String> map = new HashMap<>();
        try {
            //dbinfor.properties在工程路径下面
            odbc_driver = getProperties("dbinfor.properties", "odbc_driver");
            odbc_url = getProperties("dbinfor.properties", "odbc_url");
            username = getProperties("dbinfor.properties", "odbc_username");
            password = getProperties("dbinfor.properties", "odbc_password");
            map.put("odbc_driver", odbc_driver);
            map.put("odbc_url", odbc_url);
            map.put("oracle_username", username);
            map.put("password", password);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return map;
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
