package com.mycompany.dishcover.Util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/dishcovery_db"; // update if db name is different
    private static final String USER = "root"; // your phpMyAdmin username
    private static final String PASSWORD = ""; // your phpMyAdmin password (can be empty)

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
