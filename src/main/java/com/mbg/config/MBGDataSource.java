package com.mbg.config;

import com.codeway.daoTemplate.utils.TemplateDataSource; // Pastikan ini tidak merah
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MBGDataSource implements TemplateDataSource {

    @Override
    public Connection getConnection() throws Exception {
        // --- KITA TULIS LANGSUNG (HARDCODE) AGAR TIDAK SALAH BACA FILE ---

        // 1. Panggil Driver MySQL 8
        Class.forName("com.mysql.cj.jdbc.Driver");

        // 2. Alamat Database yang BENAR (Port 3306)
        String url = "jdbc:mysql://localhost:3306/ets_mbg?zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
        String username = "root";
        String password = ""; // Password kosong sesuai XAMPP default

        // 3. Hubungkan
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}