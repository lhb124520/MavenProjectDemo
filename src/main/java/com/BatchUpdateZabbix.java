package com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
    private static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    //数据库的用户名与密码
    private static String USER = "sendi";
    private static String PASS = "Sd_1234";
    private static String cpuParams = String.format("#!/usr/bin/sh\nsystype=`uname`\nif [ $systype = \"Linux\" ];then\n    filename=\"cpuuse_temp.txt\"\n    vmstat -w 1 3 > ~/$filename\n    idle_column=`cat ~/$filename | grep -v \"-\" | awk '/id/{for(i=1;i<=NF;i++) if($i ~ /id/) print i}'` && cpuidle=`cat ~/$filename | awk -v i=$idle_column END'{print$i}'`\n    cpu_use_precent=`expr 100 - $cpuidle`\n    echo ${cpu_use_precent%%/*}\nfi\nif [ $systype = \"HP-UX\" ];then\n    export TERM=vt100\n    top -d 1 -f cpu_info\n    cpu_idle=`cat cpu_info | grep avg | awk '{printf $6}'`\n    cpu_idle_precent=`echo ${cpu_idle%%%%\\%%*}`\n    echo \"100 - $cpu_idle_precent\" | bc\nfi\nif [ $systype = \"AIX\" ]; then\n    mpstat -a|grep ALL|awk '{printf $24+$25}'\nfi");
    private static String memParams = String.format("#!/usr/bin/sh\nsystype=`uname`\nif [ $systype = \"SunOS\" ];then\n        LANG=C\n        export LANG\n        #total=`/usr/sbin/prtconf | grep Memory | awk '{print 1024*1024*$3}'`\n        if [ -x \"/usr/platform/sun4u/sbin/prtdiag\" ];then\n           mem_str=`/usr/platform/sun4u/sbin/prtdiag | grep \"Memory size:\"`\n        else\n           mem_str=`/usr/sbin/prtdiag | grep \"Memory size:\"`\n        fi\n        ret=`echo $mem_str | grep GB`\n        if [ $? -eq 0 ]\n        then\n                total=`echo $mem_str | sed 's/GB//'| awk '{printf \"%%.0f\",$3*1024*1024*1024}'`\n        else\n                total=`echo $mem_str | sed 's/Mb//'| awk '{printf \"%%.0f\",$3*1024*1024}'`\n        fi\n        #free=`vmstat | awk '{if(NR==3){printf \"%%.0f\",$5*1024}}'`\n        free=`vmstat 1 2 | awk '{if(NR==4){printf \"%%.0f\",$5*1024}}'`\n        #swap=`vmstat | awk '{if(NR==3){print $4}}'`\n        used_percent=`echo \"\" |awk \"{printf \\\"%%.2f\\\",($total-$free)*100/$total}\"`\n        printf \"$used_percent\\n\"\nfi\nif [ $systype = \"AIX\" ];then\n        total=`lsattr -El mem0 | awk '{if(NR==1){printf \"%%.0f\",1024*1024*$2}}'`\n        free=`vmstat | awk '{if(NR==4){printf \"%%.0f\",4*1024*$4}}'`\n        used_percent=`echo \"\" |awk \"{printf \\\"%%.2f\\\",($total-$free)*100/$total}\"`\n        printf \"$used_percent\\n\"\nfi\nif [ $systype = \"HP-UX\" ];then\n        free_tmp=`vmstat 1 2 | awk '{if(NR==4){print $5}}'`\n        free_total=`echo \"\" |awk \"{printf $free_tmp/1024/1024}\"`\n        total_mem_temp=`echo \"selall;infolog\"|/usr/sbin/cstm|grep \"Total Configured Memory\"|awk '{print $5}'`\n        total_mem=0\n        for i in $total_mem_temp\n        do\n          total_mem=`expr $total_mem + $i`\n        done\n        total_mem=`echo \"\" |awk \"{printf $total_mem/1024}\"`\n        free_precent=`echo \"\" |awk \"{printf $free_total/$total_mem}\"`\n        echo \"(1.0 - $free_precent) * 100\" | bc\nfi\nif [ $systype = \"UnixWare\" ];then\n        result=`sar -r 1 2 | grep Average | awk '{print 4*$2,4*$3}'`\n        total=`/sbin/memsize | sed 's/^[ \\t]*//'| awk '{printf \"%%.0f\", $1}'`\n        free=`echo $result | awk '{printf \"%%.0f\", $1*1024}'`\n        used_percent=`echo \"\" |awk \"{printf \\\"%%.2f\\\", ($total-$free)*100/$total}\"`\n        printf \"$used_percent\\n\"\nfi\nif [ $systype = \"Linux\" ];then\n        total=`grep \"MemTotal\" /proc/meminfo | awk '{printf \"%%.0f\",$2*1024}'`\n        t=`grep \"MemTotal\" /proc/meminfo | awk '{printf $2 }'`\n        free=`grep \"MemFree\" /proc/meminfo | awk '{printf \"%%.0f\",$2*1024}'`\n        f=`grep \"MemFree\" /proc/meminfo | awk '{printf $2}'`\n        bf=`grep -E '^Buffers' /proc/meminfo | awk '{printf $2}'`\n        c=`grep -E '^Cached' /proc/meminfo | awk '{printf $2}'`\n        used_percent=`echo \"\" |awk \"{printf \\\"%%.2f\\\",($t-$f-$bf-$c)*100/$t}\"`\n        echo \"$used_percent\"\nfi");

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

        List<String> zabbixServers = getZabbixServers(file);
        if (zabbixServers.isEmpty()) {
            log.warn("没有有效的连接信息, 退出");
            return;
        }

        zabbixServers.forEach(BatchUpdateZabbix::optDb);
    }

    /**
     * 操作数据库
     */
    private static void optDb(String ip) {
        Connection connection;
        Statement statement;
        try {
            //注册JDBC驱动
            Class.forName(JDBC_DRIVER);

            //用文件的ip替换
            String jdbcUrl = "jdbc:mysql://ip:3306/zabbix?useSSL=false&serverTimezone=UTC";
            jdbcUrl = jdbcUrl.replace("ip", ip);
            //数据库的连接：通过DriverManager类的getConnection方法，传入三个参数：数据库URL、用户名、用户密码，实例化connection对象
            connection = DriverManager.getConnection(jdbcUrl, USER, PASS);

            //实例化statement对象
            statement = connection.createStatement();

            //定义数据库查询语句：查询aa表中的name、sex两列数据
//            String sql = "SELECT username FROM user";
            String sqlCpu = "UPDATE `items` SET params =" + cpuParams + " WHERE `name` = 'CPU使用率（SSH）'";
            //执行查询语句
            boolean executeCpu = statement.execute(sqlCpu);
            log.info("是否执行cpu成功：" + executeCpu);

            String sqlMem = "UPDATE `items` SET params =" + memParams + " WHERE `name` = '内存占用率（SSH）'";
            //执行查询语句
            boolean executeMem = statement.execute(sqlMem);
            log.info("是否执行内存成功：" + executeMem);
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
            statement.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getZabbixServers(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        String line;
        List<String> inputStr = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }
            inputStr.add(line.trim());
        }
        bufferedReader.close();
        log.info("读取输入文件完成");
        return inputStr;
    }
}
