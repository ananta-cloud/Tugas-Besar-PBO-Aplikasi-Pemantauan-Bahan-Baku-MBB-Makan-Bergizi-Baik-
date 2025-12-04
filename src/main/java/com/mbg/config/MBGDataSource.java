package com.mbg.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.codeway.daoTemplate.utils.TemplateConfiguration;
import com.codeway.daoTemplate.utils.TemplateDataSource;

/**
 MBGDataSource - Singleton Pattern Implementation
 Memastikan hanya ada satu instance koneksi database selama aplikasi berjalan.
**/
public class MBGDataSource implements TemplateDataSource {

    private static MBGDataSource instance;
    private Connection connection;


    // Private constructor untuk mencegah instansiasi dari luar
    private MBGDataSource() {
    }

    // Method untuk mendapatkan instance Singleton
    public static synchronized MBGDataSource getInstance() {
        if (instance == null) {
            instance = new MBGDataSource();
        }
        return instance;
    }

    @Override
    public Connection getConnection() throws Exception {
        try {
            // Memuat database
            Class.forName(TemplateConfiguration.getString("driverClassName"));
            
            // Membuat koneksi baru
            connection = DriverManager.getConnection(
                TemplateConfiguration.getString("db.connection.url"),
                TemplateConfiguration.getString("db.connection.username"),
                TemplateConfiguration.getString("db.connection.checksum")
            );
            
            return connection;
        } catch (ClassNotFoundException e) {
            throw new Exception("Database driver tidak ditemukan: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new Exception("Gagal membuat koneksi database: " + e.getMessage(), e);
        }
    }

    // Menutup koneksi database
    @Override
    public void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
                System.out.println("Koneksi database ditutup");
            } catch (SQLException e) {
                System.err.println("Error saat menutup koneksi: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Method untuk reset instance (opsional, untuk testing)
    public static synchronized void resetInstance() {
        if (instance != null && instance.connection != null) {
            try {
                instance.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        instance = null;
    }
}