package com.bink.utils;

import com.bink.base.TestBase;

import java.util.ArrayList;

import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @Desc 数据库连接类，包含基本方法
 * @Author by Bink.
 * Created on 2019/12/6.
 */

public class DataBase extends TestBase {

    static String DB_URL_ALI;
    static String USER_ALI;
    static String PASS_ALI;
    static String JDBC_DRIVER;

    /**
     * 配置数据库连接
     *
     * @param configFileName 配置文件名称，省略文件后缀
     * @param url            数据库地址
     * @param user           用户名
     * @param password       密码
     * @param driver         驱动名称
     */
    public static void setDBConfig(String configFileName,
                                   String url,
                                   String user,
                                   String password,
                                   String driver) {
        ResourceBundle rb = ResourceBundle.getBundle(configFileName);
        DB_URL_ALI = rb.getString(url);
        USER_ALI = rb.getString(user);
        PASS_ALI = rb.getString(password);
        JDBC_DRIVER = rb.getString(driver);
    }

    /**
     * 获取数据库连接对象
     *
     * @return 获取数据库链接
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
//            Class.forName("com.mysql.jdbc.Driver");
            setDBConfig("jdbc", "url", "username", "password", "driver");
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL_ALI, USER_ALI, PASS_ALI);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 数据库更新操作方法
     * 插入、修改、删除
     *
     * @param updateSQL
     */
    public static void update(String updateSQL) {
        Connection connection = getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            System.out.println("开始执行更新SQL:" + updateSQL);
            int resultSet = statement.executeUpdate(updateSQL);
            System.out.println("更新SQL执行影响行数： " + resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            statement.close();
            connection.close();
            System.out.println("关闭数据库链接");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据库查询操作方法
     *
     * @param selectSQL  待执行的SQL
     * @param columnName 需要返回的字段
     * @return returnValue  返回值
     */
    public static String query(String selectSQL, String columnName) {
        Connection connection = getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        String returnValue = null;
        try {
            statement = connection.createStatement();
            System.out.println("开始查询SQL： " + selectSQL);
            resultSet = statement.executeQuery(selectSQL);
            while (resultSet.next()) {
                returnValue = resultSet.getString(columnName);
                System.out.println("Result： " + returnValue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            resultSet.close();
            statement.close();
            connection.close();
            System.out.println("关闭数据库链接");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    /**
     * @param selectSQL  查询SQL语句
     * @param columnName 所需字段
     * @return
     */
    public static List<String> queryList(String selectSQL, String columnName) {
        Connection connection = getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        List<String> returnValue = new ArrayList<String>();
        try {
            statement = connection.createStatement();
            System.out.println("开始查询SQL： " + selectSQL);
            resultSet = statement.executeQuery(selectSQL);
            while (resultSet.next()) {
                String returnValue1 = resultSet.getString(columnName);
                System.out.println(returnValue1);
                returnValue.add(returnValue1);
                System.out.println("Result： " + returnValue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            resultSet.close();
            statement.close();
            connection.close();
            System.out.println("关闭数据库链接");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

}



