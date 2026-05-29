package frontend.controllers;

import frontend.models.Transaction;
import frontend.models.TransactionState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class HistoryController implements Initializable {

    @FXML
    private ComboBox<String> categoryFilter;
    @FXML
    private ComboBox<String> dateFilter;
    @FXML
    private Button exportButton;

    // Table
    @FXML
    private TableView<Transaction> transactionTable;
    @FXML
    private TableColumn<Transaction, String> dateColumn;
    @FXML
    private TableColumn<Transaction, String> categoryColumn;
    @FXML
    private TableColumn<Transaction, String> typeColumn;
    @FXML
    private TableColumn<Transaction, String> notesColumn;
    @FXML
    private TableColumn<Transaction, Number> amountColumn;

    @FXML
    private Label paginationLabel;

    private FilteredList<Transaction> filteredData;
    private String searchQuery = "";

    private static final String STYLE_LEFT = "-fx-alignment: CENTER-LEFT;";
    private static final String STYLE_RIGHT = "-fx-alignment: CENTER-RIGHT;";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();
        setupFilters();

        // 1. Wrap the master transaction list in a FilteredList
        filteredData = new FilteredList<>(TransactionState.getInstance().getTransactions(), p -> true);

        // 2. Bind the table items
        transactionTable.setItems(filteredData);

        // 3. Listen to filter events
        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> updateFilters());
        dateFilter.valueProperty().addListener((obs, oldVal, newVal) -> updateFilters());

        // 4. Load initial details
        updateFilters();
    }

    private void setupColumns() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Date Column
        dateColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle(empty || item == null ? "" : STYLE_LEFT);
            }
        });

        // Category Badge Column
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

        // Type Column
        typeColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle(empty || item == null ? "" : STYLE_LEFT);
            }
        });

        // Notes Column
        notesColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle(empty || item == null ? "" : STYLE_LEFT);
            }
        });

        // Amount Column with colors
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

        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupFilters() {
        categoryFilter.setItems(FXCollections.observableArrayList(
                "All Categories", "Salary", "Software", "Revenue", "Travel",
                "Marketing", "Office", "Housing", "Food & Dining", "Transport", "Other"));
        categoryFilter.setValue("All Categories");

        dateFilter.setItems(FXCollections.observableArrayList(
                "Last 30 Days", "Last 7 Days", "All Time"));
        dateFilter.setValue("All Time");
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query == null ? "" : query.trim();
        updateFilters();
    }

    private void updateFilters() {
        filteredData.setPredicate(t -> {
            // 1. Text Search Filter
            if (!searchQuery.isEmpty()) {
                String lower = searchQuery.toLowerCase();
                boolean matches = t.getNotes().toLowerCase().contains(lower)
                        || t.getCategory().toLowerCase().contains(lower)
                        || t.getType().toLowerCase().contains(lower)
                        || t.getDate().toLowerCase().contains(lower);
                if (!matches)
                    return false;
            }

            // 2. Category Filter
            String cat = categoryFilter.getValue();
            if (cat != null && !cat.equalsIgnoreCase("All Categories")) {
                if (!t.getCategory().equalsIgnoreCase(cat))
                    return false;
            }

            // 3. Date Filter — Last 7 Days / Last 30 Days
            String dateRange = dateFilter.getValue();
            if (dateRange != null && !dateRange.equalsIgnoreCase("All Time")) {
                try {
                    LocalDate txDate = LocalDate.parse(t.getDate());
                    LocalDate cutoff = dateRange.equalsIgnoreCase("Last 7 Days")
                            ? LocalDate.now().minusDays(7)
                            : LocalDate.now().minusDays(30);
                    if (txDate.isBefore(cutoff)) return false;
                } catch (Exception ignored) {
                    // If date can't be parsed, include the row
                }
            }

            return true;
        });

        // Update indicators
        int total = filteredData.size();
        paginationLabel.setText(String.format("Showing 1-%d of %d entries", total, total));
    }

    @FXML
    private void handleExport(ActionEvent event) {
        // Show a save dialog so the user can choose where to save the file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Transactions to CSV");
        fileChooser.setInitialFileName("finance_tracker_export.csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        Stage stage = (Stage) exportButton.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file == null) {
            // User cancelled the dialog — do nothing
            return;
        }

        // Write the filtered transactions currently shown in the table
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // CSV header
            writer.write("Date,Category,Type,Notes,Amount,Status");
            writer.newLine();

            for (Transaction t : filteredData) {
                // Wrap fields in quotes to handle commas inside notes
                String line = String.format("\"%s\",\"%s\",\"%s\",\"%s\",%,.2f,\"%s\"",
                        t.getDate(),
                        t.getCategory(),
                        t.getType(),
                        t.getNotes().replace("\"", "\"\""), // escape quotes
                        t.getAmount(),
                        t.getStatus());
                writer.write(line);
                writer.newLine();
            }

            // Success feedback
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Successful");
            alert.setHeaderText(null);
            alert.setContentText(
                    filteredData.size() + " transaction(s) exported to:\n" + file.getAbsolutePath());
            DialogPane pane = alert.getDialogPane();
            pane.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            alert.showAndWait();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Failed");
            alert.setHeaderText(null);
            alert.setContentText("Could not write file:\n" + e.getMessage());
            alert.showAndWait();
        }
    }
}
