package frontend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for obtaining a connection to the MySQL database.
 *
 * <p>Each call to {@link #getKoneksi()} opens a new physical connection.
 * For a small desktop application this is acceptable, but a connection pool
 * (e.g. HikariCP) should be considered if the number of concurrent operations grows.
 */
public class ConnectDB {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/finance_db";
    private static final String USER = "root";
    private static final String PASS = "";

    /** Prevent instantiation — this is a static utility class. */
    private ConnectDB() {}

    /**
     * Opens and returns a new {@link Connection} to the finance database.
     *
     * <p>The caller is responsible for closing the connection (preferably via
     * try-with-resources) to avoid resource leaks.
     *
     * @return a live {@link Connection}, or {@code null} if the connection fails
     */
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
