package frontend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    // Sesuaikan dengan nama database kamu di phpMyAdmin
    private static final String DB_URL = "jdbc:mysql://localhost:3306/finance_db";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getKoneksi() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Koneksi ke database berhasil!");
        } catch (SQLException e) {
            System.out.println("Koneksi Gagal: " + e.getMessage());
        }
        return conn;
    }
}
