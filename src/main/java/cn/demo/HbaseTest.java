package cn.demo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

/**
 * 描述:
 *
 * @author : lhb
 * @date : 2020-08-11 15:25
 */
public class HbaseTest {
    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        String ip = "c5a1.ecld.com,c5m1.ecld.com,c5m2.ecld.com,c4a1.ecld.com,c4m1.ecld.com,c4m2.ecld.com,c8a1.ecld.com,c8m1.ecld.com,c8m2.ecld.com";//"192.168.204.214,192.168.204.242,192.168.204.215";10.134.161.107
        String port = "2181";
//        System.setProperty("java.security.krb5.realm", "ECLD.COM");
//        System.setProperty("java.security.krb5.kdc","132.121.98.74");
        conf.set("hbase.zookeeper.property.maxclientcnxns", "300");
        conf.set("hbase.ipc.client.socket.timeout.connect","1000");
        conf.set("zookeeper.session.timeout", "500");
        conf.set("hbase.regionserver.handler.count", "500");
        System.setProperty("java.security.krb5.conf","/home/itzxyy/krb5.conf");//windows 环境可以将此文件改成krb5.ini放到C:/Windows目录下；Linux环境放到/etc/文件下，改成krb5.sh文件，系统会自动加载
        conf.set("hadoop.security.authentication","kerberos");
        conf.set("hbase.master.kerberos.principal","hbase/_HOST@ECLD.COM");//从Hbase-site.xml文件中获取配置信息
        conf.set("hbase.regionserver.kerberos.principal","hbase/_HOST@ECLD.COM");//从Hbase-site.xml文件中获取配置信息
        conf.set("hbase.zookeeper.property.clientPort",port);
        conf.set("hbase.security.authentication","kerberos");
        conf.set("hbase.zookeeper.quorum",ip);
        UserGroupInformation.setConfiguration(conf);
        try{
            UserGroupInformation.loginUserFromKeytab("itmp/132.121.109.86@ECLD.COM","/home/itzxyy/itmp.132.121.109.86.keytab");//认证用户部分
        }catch(IOException e) {
            //TODOAuto-generated catch block
            e.printStackTrace();
        }

        conf = HBaseConfiguration.create(conf);
        Connection connection = ConnectionFactory.createConnection(conf);
        TableName tableName = TableName.valueOf("itmp");
        boolean itmp = connection.getAdmin().tableExists(tableName);
        System.out.println(itmp);
        connection.close();
    }
}
