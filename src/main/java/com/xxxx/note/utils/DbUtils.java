package com.xxxx.note.utils;

import java.sql.*;
import java.util.Properties;

public class DbUtils {
    private  static  Properties properties=new Properties();

    /**
     * 静态代码块
     */
    static{
        try {
            //加载配置文件
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties"));
            //获取驱动
            Class.forName(properties.getProperty("jbdcName"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取数据库连接
     * @return
     */
    public static Connection getConnection(){
        Connection conn=null;
        try {//获取连接数据库的相关信息
            conn= DriverManager.getConnection(
                    properties.getProperty("dbUrl"),
                    properties.getProperty("dbName"),
                    properties.getProperty("dbPwd")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }

    /**
     * 关闭资源
     * @param resultSet
     * @param connection
     * @param preparedStatement
     */
    public static void close(ResultSet resultSet, Connection connection, PreparedStatement preparedStatement){
        //关闭连接
            try {
                if(resultSet!=null) {
                    resultSet.close();
                }
                if(connection!=null) {
                    connection.close();
                }
                if(preparedStatement!=null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }
}
