package com.mbg.config;

import com.codeway.daoTemplate.utils.TemplateConfiguration;
import com.codeway.daoTemplate.utils.TemplateDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * MBGDataSource - Singleton Pattern Implementation
 * Memastikan hanya ada satu instance koneksi database selama aplikasi berjalan.
 * Singleton pattern digunakan untuk:
 * - Efisiensi sumber daya (hanya 1 koneksi)
 * - Konsistensi data di seluruh modul
 * - Manajemen transaksi terpusat
 */
public class MBGDataSource implements TemplateDataSource {

    private static MBGDataSource instance;
    private Connection connection;

    /**
     * Private constructor untuk mencegah instantiasi dari luar
     */
    public MBGDataSource() {
    }

    /**
     * Method untuk mendapatkan instance Singleton
     * Thread-safe double-checked locking
     * 
     * @return instance MBGDataSource yang unik
     */
    public static synchronized MBGDataSource getInstance() {
        if (instance == null) {
            instance = new MBGDataSource();
        }
        return instance;
    }

    /**
     * Mendapatkan koneksi database
     * Setiap kali dipanggil, membuat koneksi baru (tidak cache)
     * 
     * @return Connection object
     * @throws Exception jika driver tidak ditemukan atau koneksi gagal
     */
    @Override
    public Connection getConnection() throws Exception {
        try {
            // Memuat driver database
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

    /**
     * Menutup koneksi database
     * 
     * @param con Connection yang akan ditutup
     */
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

    /**
     * Method untuk reset instance (opsional, untuk testing)
     */
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