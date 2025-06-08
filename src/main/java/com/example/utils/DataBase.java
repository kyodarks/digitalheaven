package com.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
    private static Connection connection;

    private final String url = "localhost:3306/digital_heaven";
    private final String user = "root";
    private final String pass = "";

    public DataBase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + url, user, pass);

            System.out.println("Conexion exitosa");
        } catch (SQLException e) {
            System.out.println("Fallo la conexion a la base de datos! | " + e.getMessage());
        }
    }

    public static Connection getConnection(){return connection;}

    public static ResultSet executeQuery(String sql) {
        try {
            Statement statement = connection.createStatement();

            return statement.executeQuery(sql);
        } catch (SQLException e) {}

        return null;
    }
}
