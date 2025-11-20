package com.mbb.eas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3308/pbo-mbb";
        String user = "root";
        String pass = "";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("Connected!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
