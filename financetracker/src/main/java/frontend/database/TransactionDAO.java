package frontend.database;

import frontend.models.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class TransactionDAO {

    public static boolean insertTransaction(Transaction trx) {
        // Query SQL untuk memasukkan data ke tabel
        String query = "INSERT INTO transaksi (tanggal, kategori, jenis, catatan, jumlah, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectDB.getKoneksi();
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Konversi tanggal dari String ke format Date SQL
            Date sqlDate = Date.valueOf(trx.getDate());

            // Isi tanda tanya (?) pada query dengan data dari object Transaction
            pstmt.setDate(1, sqlDate);
            pstmt.setString(2, trx.getCategory());
            pstmt.setString(3, trx.getType());
            pstmt.setString(4, trx.getNotes());
            pstmt.setDouble(5, trx.getAmount());
            pstmt.setString(6, trx.getStatus());

            // Eksekusi query
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        trx.setId(rs.getInt(1)); // Mengisi ID dari database ke memori Java
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.out.println("Gagal menyimpan transaksi: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println("Format tanggal salah! Wajib YYYY-MM-DD.");
            return false;
        }
    }

    public static ObservableList<Transaction> getAllTransactions() {
        ObservableList<Transaction> list = FXCollections.observableArrayList();

        // Query untuk mengambil semua data, diurutkan dari ID terbesar (terbaru) ke
        // terkecil (terlama)
        String query = "SELECT * FROM transaksi ORDER BY id DESC";

        try (Connection conn = ConnectDB.getKoneksi();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Ambil nilai dari setiap kolom di database
                int id = rs.getInt("id"); // column id
                String tanggal = rs.getString("tanggal"); // Otomatis format yyyy-MM-dd
                String kategori = rs.getString("kategori");
                String jenis = rs.getString("jenis");
                String catatan = rs.getString("catatan");
                double jumlah = rs.getDouble("jumlah");
                String status = rs.getString("status");

                // Bungkus kembali ke dalam model Transaction
                Transaction trx = new Transaction(tanggal, kategori, jenis, catatan, jumlah, status);
                trx.setId(id); // set id
                list.add(trx);
            }
        } catch (SQLException e) {
            System.out.println("Gagal menarik data dari database: " + e.getMessage());
        }

        return list;
    }

    public static boolean deleteTransaction(int id) {
        String query = "DELETE FROM transaksi WHERE id = ?";

        try (Connection conn = ConnectDB.getKoneksi();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id); // Mengisi "?" dengan id transaksi
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Gagal menghapus data: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateTransaction(Transaction trx) {
        String query = "UPDATE transaksi SET tanggal=?, kategori=?, jenis=?, catatan=?, jumlah=?, status=? WHERE id=?";

        try (Connection conn = ConnectDB.getKoneksi();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDate(1, java.sql.Date.valueOf(trx.getDate())); // Format yyyy-MM-dd
            pstmt.setString(2, trx.getCategory());
            pstmt.setString(3, trx.getType());
            pstmt.setString(4, trx.getNotes());
            pstmt.setDouble(5, trx.getAmount());
            pstmt.setString(6, trx.getStatus());

            pstmt.setInt(7, trx.getId()); // Kunci utamanya: ID transaksi

            return pstmt.executeUpdate() > 0;

        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Gagal mengupdate data: " + e.getMessage());
            return false;
        }
    }
}