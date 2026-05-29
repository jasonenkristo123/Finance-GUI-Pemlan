package frontend.controllers;

import frontend.models.Transaction;
import frontend.models.TransactionState;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label balanceLabel;
    @FXML
    private Label incomeLabel;
    @FXML
    private Label expenseLabel;

    @FXML
    private Button viewAllBtn;

    // Table
    @FXML
    private TableView<Transaction> recentTable;
    @FXML
    private TableColumn<Transaction, String> categoryColumn;
    @FXML
    private TableColumn<Transaction, String> dateColumn;
    @FXML
    private TableColumn<Transaction, Number> amountColumn;
    @FXML
    private TableColumn<Transaction, String> statusColumn;

    // Donut Chart
    @FXML
    private PieChart expensePieChart;
    @FXML
    private Label donutTotalLabel;

    // Breakdown Percentages
    @FXML
    private Label housingPercentLabel;
    @FXML
    private Label foodPercentLabel;
    @FXML
    private Label transportPercentLabel;
    @FXML
    private Label otherPercentLabel;

    private MainController mainController;

    private static final String STYLE_LEFT = "-fx-alignment: CENTER-LEFT;";
    private static final String STYLE_RIGHT = "-fx-alignment: CENTER-RIGHT;";

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();

        // Load initial values
        updateDashboardData();

        // Listen to changes in the global transaction state to auto-update
        TransactionState.getInstance().getTransactions().addListener((ListChangeListener<Transaction>) change -> {
            updateDashboardData();
        });
    }

    private void setupColumns() {
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Category with Emoji Badge
        categoryColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("");
                    return;
                }
                setGraphic(CategoryBadgeUtil.createBadge(item));
                setText(null);
                setStyle(STYLE_LEFT);
            }
        });

        // Date
        dateColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle(empty || item == null ? "" : STYLE_LEFT);
            }
        });

        // Amount with +/- and Red/Green Colors
        amountColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                double val = item.doubleValue();
                setText(String.format("%s$%,.2f", val >= 0 ? "+" : "-", Math.abs(val)));
                setStyle(STYLE_RIGHT + " -fx-text-fill: " + (val >= 0 ? "#4ADE80" : "#F87171") + ";");
            }
        });

        // Status with Completed/Pending Badges
        statusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("");
                    return;
                }
                Label badge = new Label(item);
                String subClass = item.equalsIgnoreCase("completed") ? "completed" : "pending";
                badge.getStyleClass().addAll("status-badge", "badge-" + subClass);
                setGraphic(badge);
                setText(null);
                setStyle(STYLE_LEFT);
            }
        });

        recentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void updateDashboardData() {
        TransactionState state = TransactionState.getInstance();

        // 1. Update Metrics Cards
        balanceLabel.setText(String.format("$%,.2f", state.getCreditBalance()));
        incomeLabel.setText(String.format("$%,.2f", state.getTotalIncome()));
        expenseLabel.setText(String.format("$%,.2f", state.getTotalExpense()));
        donutTotalLabel.setText(String.format("$%,.0f", state.getTotalExpense()));

        // 2. Load the 5 most recent transactions
        ObservableList<Transaction> all = state.getTransactions();
        int limit = Math.min(5, all.size());
        ObservableList<Transaction> recent = FXCollections.observableArrayList(all.subList(0, limit));
        recentTable.setItems(recent);

        // 3. Update Chart & Percentages
        double totalExpense = state.getTotalExpense();
        if (totalExpense <= 0) {
            expensePieChart.setData(FXCollections.observableArrayList());
            housingPercentLabel.setText("0%");
            foodPercentLabel.setText("0%");
            transportPercentLabel.setText("0%");
            otherPercentLabel.setText("0%");
            return;
        }

        // Calculate actual expense categories
        double housing = state.getExpenseByCategory("housing");
        double food = state.getExpenseByCategory("food & dining") + state.getExpenseByCategory("food");
        double transport = state.getExpenseByCategory("transport") + state.getExpenseByCategory("travel");

        // "Other" is total expenses minus housing, food, and transport
        double other = totalExpense - (housing + food + transport);
        if (other < 0)
            other = 0;

        // Calculate percentages
        int pHousing = (int) Math.round((housing / totalExpense) * 100);
        int pFood = (int) Math.round((food / totalExpense) * 100);
        int pTransport = (int) Math.round((transport / totalExpense) * 100);
        int pOther = 100 - (pHousing + pFood + pTransport);
        if (pOther < 0)
            pOther = 0;

        housingPercentLabel.setText(pHousing + "%");
        foodPercentLabel.setText(pFood + "%");
        transportPercentLabel.setText(pTransport + "%");
        otherPercentLabel.setText(pOther + "%");

        // Set Chart Data
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList(
                new PieChart.Data("Housing", housing),
                new PieChart.Data("Food", food),
                new PieChart.Data("Transport", transport),
                new PieChart.Data("Other", other));
        expensePieChart.setData(chartData);
    }

    @FXML
    private void handleViewAll(ActionEvent event) {
        if (mainController != null) {
            mainController.navigateToHistory();
        }
    }

    @FXML
    private void handleTombolEdit(ActionEvent event) {
        // 1. Ambil data yang diklik di tabel
        Transaction selectedTrx = recentTable.getSelectionModel().getSelectedItem();

        if (selectedTrx != null) {
            // 2. Suruh MainController pindah halaman SAMBIL membawa data transaksinya
            if (mainController != null) {
                mainController.goToEditTransaction(selectedTrx);
            }
        } else {
            System.out.println("Peringatan: Pilih dulu transaksi di tabel yang mau diedit!");
        }
    }

    @FXML
    private void handleTombolDelete(ActionEvent event) {
        // 1. Ambil data yang sedang di-klik/dipilih oleh user di tabel
        Transaction selectedTrx = recentTable.getSelectionModel().getSelectedItem();

        // 2. Cek apakah user benar-benar memilih data (bukan klik ruang kosong)
        if (selectedTrx != null) {

            // 3. Eksekusi hapus di Database MySQL lewat DAO
            boolean isDeleted = frontend.database.TransactionDAO.deleteTransaction(selectedTrx.getId());

            if (isDeleted) {
                // 4. Jika sukses di MySQL, hapus juga dari memori UI lewat abstraksi state
                TransactionState.getInstance().removeTransaction(selectedTrx);

                System.out.println("Sukses: Data berhasil dihapus dari database!");

                // Catatan: Layar akan otomatis ter-update karena di fungsi initialize() kamu
                // sudah memasang listener (ListChangeListener) yang memanggil
                // updateDashboardData().
            } else {
                System.out.println("Error: Gagal menghapus data dari database.");
            }

        } else {
            System.out.println("Peringatan: Pilih transaksi di tabel terlebih dahulu!");
        }
    }
}
