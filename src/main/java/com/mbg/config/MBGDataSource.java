package com.mbg.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.codeway.daoTemplate.utils.TemplateConfiguration;
import com.codeway.daoTemplate.utils.TemplateDataSource;

/**
 MBGDataSource - Singleton Pattern Implementation (Revised)
 Memastikan hanya ada satu instance koneksi database selama aplikasi berjalan.
 Koneksi dibuat sekali dan direuse, bukan dibuat ulang setiap kali dipanggil.
**/
public class MBGDataSource implements TemplateDataSource {

    private static MBGDataSource instance;
    private Connection connection;
    private boolean isConnected = false;


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
            // Jika koneksi sudah ada dan masih aktif, return koneksi yang sama
            if (connection != null && !connection.isClosed() && isConnected) {
                System.out.println("Menggunakan koneksi yang sudah ada");
                return connection;
            }

            // Jika koneksi belum ada atau sudah ditutup, buat koneksi baru
            System.out.println("Membuat koneksi database baru...");
            Class.forName(TemplateConfiguration.getString("driverClassName"));
            
            connection = DriverManager.getConnection(
                TemplateConfiguration.getString("db.connection.url"),
                TemplateConfiguration.getString("db.connection.username"),
                TemplateConfiguration.getString("db.connection.checksum")
            );
            
            isConnected = true;
            System.out.println("Koneksi database berhasil dibuat");
            return connection;

        } catch (ClassNotFoundException e) {
            isConnected = false;
            throw new Exception("Database driver tidak ditemukan: " + e.getMessage(), e);
        } catch (SQLException e) {
            isConnected = false;
            throw new Exception("Gagal membuat koneksi database: " + e.getMessage(), e);
        }
    }

    // Menutup koneksi database
    @Override
    public void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
                isConnected = false;
                System.out.println("Koneksi database ditutup");
            } catch (SQLException e) {
                System.err.println("Error saat menutup koneksi: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Method untuk reset instance
    public static synchronized void resetInstance() {
        if (instance != null && instance.connection != null) {
            try {
                instance.connection.close();
                instance.isConnected = false;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        instance = null;
    }

    // Method untuk check status koneksi
    public boolean isConnected() {
        return isConnected;
    }
}