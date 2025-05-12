package com.example.miniprojekt.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres"; // Preveri, če je to pravi URL za tvojo bazo
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres"; // Zamenjaj z dejanskim geslom

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        if (conn != null) {
            System.out.println("Uspešna povezava na bazo!");
        } else {
            System.out.println("Napaka pri povezavi z bazo.");
        }
        return conn;
    }
}
