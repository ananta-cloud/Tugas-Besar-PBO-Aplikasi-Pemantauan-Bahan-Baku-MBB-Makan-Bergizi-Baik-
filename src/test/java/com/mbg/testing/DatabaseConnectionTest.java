package com.mbg.testing;

import com.mbg.config.MBGDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

public class DatabaseConnectionTest {

    @Test
    @DisplayName("Tes Koneksi ke Database MySQL")
    void testConnection() {
        MBGDataSource dataSource = new MBGDataSource();
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("Mencoba terhubung ke database...");
            
            // Assertion: Memastikan koneksi tidak null
            Assertions.assertNotNull(connection, "Koneksi database gagal (objek null)");
            
            // Assertion: Memastikan koneksi valid
            Assertions.assertFalse(connection.isClosed(), "Koneksi database tertutup");
            
            System.out.println("Sukses! Terhubung ke: " + connection.getCatalog());
        } catch (Exception e) {
            // Jika terjadi error, test dianggap gagal
            Assertions.fail("Terjadi Error Koneksi: " + e.getMessage());
        }
    }
}