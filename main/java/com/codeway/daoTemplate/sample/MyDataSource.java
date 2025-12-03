package com.codeway.daoTemplate.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.codeway.daoTemplate.utils.TemplateDataSource;

/*
*
* @Author Abhishek.Pandey
*/
public class MyDataSource  implements TemplateDataSource{

	
	private static final String URL = "jdbc:mysql://localhost:3306/ets_mbg?zeroDateTimeBehavior=convertToNull";
    private static final String USER = "root"; // Sesuaikan dengan DB Anda
    private static final String PASS = "";     // Sesuaikan dengan DB Anda

    @Override
    public Connection getConnection() throws Exception {
        // Memuat driver MySQL (pastikan mysql-connector-java ada di dependency)
        Class.forName("com.mysql.cj.jdbc.Driver"); 
        return DriverManager.getConnection(URL, USER, PASS);
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
