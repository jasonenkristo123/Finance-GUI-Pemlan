package frontend.controllers;

import frontend.models.Transaction;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // ── Navigasi Sidebar ──────────────────────────────────────────
    @FXML private Button dashboardBtn;
    @FXML private Button addTransactionBtn;
    @FXML private Button historyBtn;

    // ── Halaman Konten ────────────────────────────────────────────
    @FXML private VBox dashboardPage;
    @FXML private VBox addTransactionPage;
    @FXML private VBox historyPage;

    // ── History Page: Filter & Export ─────────────────────────────
    @FXML private Button      exportButton;
    @FXML private ComboBox<?> categoryFilter;
    @FXML private ComboBox<?> dateFilter;

    // ── History Page: Table ───────────────────────────────────────
    @FXML private TableView<Transaction>            transactionTable;
    @FXML private TableColumn<Transaction, String>  dateColumn;
    @FXML private TableColumn<Transaction, String>  categoryColumn;
    @FXML private TableColumn<Transaction, String>  typeColumn;
    @FXML private TableColumn<Transaction, String>  notesColumn;
    @FXML private TableColumn<Transaction, Number>  amountColumn;

    // Inline style — prioritas tertinggi, mengalahkan AtlantaFX stylesheet
    private static final String STYLE_LEFT  = "-fx-alignment: CENTER-LEFT;";
    private static final String STYLE_RIGHT = "-fx-alignment: CENTER-RIGHT;";

    // ── Lifecycle ─────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();
        loadDummyData();

        // Tampilkan halaman default (Dashboard) saat pertama buka
        showPage(dashboardPage, dashboardBtn);

        // Jalankan setelah scene fully rendered agar ComboBox height sudah resolved
        Platform.runLater(this::syncExportButtonHeight);
    }

    // ── Navigasi ──────────────────────────────────────────────────

    /**
     * Handler tunggal untuk semua tombol navigasi sidebar.
     * Hubungkan ketiga tombol ke method ini via onAction="#handleNavigation" di FXML.
     */
    @FXML
    private void handleNavigation(ActionEvent event) {
        Button clicked = (Button) event.getSource();

        if (clicked == dashboardBtn) {
            showPage(dashboardPage, dashboardBtn);
        } else if (clicked == addTransactionBtn) {
            showPage(addTransactionPage, addTransactionBtn);
        } else if (clicked == historyBtn) {
            showPage(historyPage, historyBtn);
        }
    }

    /**
     * Sembunyikan semua halaman, tampilkan hanya yang dipilih,
     * dan update style "active" pada tombol sidebar.
     */
    private void showPage(VBox targetPage, Button targetBtn) {
        // Reset semua halaman
        setPageActive(dashboardPage,      false);
        setPageActive(addTransactionPage, false);
        setPageActive(historyPage,        false);

        // Reset semua tombol
        dashboardBtn.getStyleClass().remove("active");
        addTransactionBtn.getStyleClass().remove("active");
        historyBtn.getStyleClass().remove("active");

        // Aktifkan target
        setPageActive(targetPage, true);
        if (!targetBtn.getStyleClass().contains("active")) {
            targetBtn.getStyleClass().add("active");
        }
    }

    private void setPageActive(VBox page, boolean isActive) {
        page.setVisible(isActive);
        page.setManaged(isActive);
    }

    // ── Export Button Height Sync ─────────────────────────────────

    private void syncExportButtonHeight() {
        if (exportButton == null || categoryFilter == null) return;
        double h = categoryFilter.getHeight();
        if (h > 0) {
            exportButton.setPrefHeight(h);
            exportButton.setMinHeight(h);
            exportButton.setMaxHeight(h);
        } else {
            exportButton.prefHeightProperty().bind(categoryFilter.heightProperty());
            exportButton.minHeightProperty().bind(categoryFilter.heightProperty());
            exportButton.maxHeightProperty().bind(categoryFilter.heightProperty());
        }
    }

    // ── Table Setup ───────────────────────────────────────────────

    private void setupColumns() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Date — CENTER_LEFT
        dateColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle(empty || item == null ? "" : STYLE_LEFT);
            }
        });

        // Category — badge + CENTER_LEFT
        categoryColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null); setText(null); setStyle(""); return;
                }
                String emoji = switch (item) {
                    case "Software"  -> "\uD83D\uDCBB";
                    case "Revenue"   -> "\uD83C\uDFE6";
                    case "Travel"    -> "\u2708";
                    case "Marketing" -> "\uD83D\uDCE2";
                    case "Office"    -> "\uD83C\uDFE2";
                    default          -> "\uD83D\uDCCC";
                };
                Label badge = new Label(emoji + "  " + item);
                badge.getStyleClass().addAll("category-badge", "badge-" + item.toLowerCase());
                setGraphic(badge);
                setText(null);
                setStyle(STYLE_LEFT);
            }
        });

        // Type — CENTER_LEFT
        typeColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle(empty || item == null ? "" : STYLE_LEFT);
            }
        });

        // Notes — CENTER_LEFT
        notesColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle(empty || item == null ? "" : STYLE_LEFT);
            }
        });

        // Amount — CENTER_RIGHT + warna
        amountColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                double val = item.doubleValue();
                setText(String.format("%s$%,.2f", val >= 0 ? "+" : "-", Math.abs(val)));
                setStyle(STYLE_RIGHT + " -fx-text-fill: " + (val >= 0 ? "#4ADE80" : "#F87171") + ";");
            }
        });

        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ── Dummy Data ────────────────────────────────────────────────

    private void loadDummyData() {
        ObservableList<Transaction> data = FXCollections.observableArrayList(
                new Transaction("Oct 24, 2023", "Software",  "Subscription",   "AWS Cloud Hosting Monthly",       -1240.50),
                new Transaction("Oct 23, 2023", "Revenue",   "Wire Transfer",  "Q3 Client Retainer - Acme Corp",  15000.00),
                new Transaction("Oct 21, 2023", "Travel",    "Corporate Card", "Delta Airlines - Tech Conf",        -850.00),
                new Transaction("Oct 20, 2023", "Revenue",   "Direct Deposit", "Product Launch Campaign",           8500.00),
                new Transaction("Oct 18, 2023", "Software",  "Subscription",   "GitHub Enterprise License",         -450.00),
                new Transaction("Oct 15, 2023", "Marketing", "Invoice",        "Social Media Ad Campaign Q4",      -2300.00),
                new Transaction("Oct 12, 2023", "Revenue",   "Wire Transfer",  "Consulting Fee - Beta Inc",         6750.00),
                new Transaction("Oct 10, 2023", "Office",    "Corporate Card", "Office Supplies & Equipment",       -380.25)
        );
        transactionTable.setItems(data);
    }
}