package com.mbg.config;

import com.codeway.daoTemplate.utils.TemplateConfiguration;
import com.codeway.daoTemplate.utils.TemplateDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MBGDataSource implements TemplateDataSource {

    @Override
    public Connection getConnection() throws Exception {
        // Memuat driver
        Class.forName(TemplateConfiguration.getString("driverClassName"));
        
        // Membuat koneksi
        return DriverManager.getConnection(
            TemplateConfiguration.getString("db.connection.url"),
            TemplateConfiguration.getString("db.connection.username"),
            TemplateConfiguration.getString("db.connection.checksum") // Di file properties ini field password
        );
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