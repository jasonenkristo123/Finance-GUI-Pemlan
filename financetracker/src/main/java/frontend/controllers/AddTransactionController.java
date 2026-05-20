package frontend.controllers;

import frontend.models.Transaction;
import frontend.models.TransactionState;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AddTransactionController implements Initializable {

    @FXML private ComboBox<String> typeInput;
    @FXML private TextField amountInput;
    @FXML private ComboBox<String> categoryInput;
    @FXML private DatePicker dateInput;
    @FXML private TextArea notesInput;

    @FXML private Label validationErrorLabel;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Populate inputs
        typeInput.setItems(FXCollections.observableArrayList("Income", "Expense"));
        categoryInput.setItems(FXCollections.observableArrayList(
                "Salary", "Software", "Revenue", "Travel", "Marketing",
                "Office", "Housing", "Food & Dining", "Transport", "Other"
        ));

        // Default setup
        resetForm();
    }

    @FXML
    private void handleSave(ActionEvent event) {
        // Clear previous error
        showError(null);

        // Validation
        String type = typeInput.getValue();
        if (type == null) {
            showError("Please select a transaction type.");
            return;
        }

        String amountText = amountInput.getText();
        if (amountText == null || amountText.trim().isEmpty()) {
            showError("Please enter an amount.");
            return;
        }

        double rawAmount;
        try {
            rawAmount = Double.parseDouble(amountText.replace(",", ""));
            if (rawAmount <= 0) {
                showError("Amount must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Invalid amount format. Please enter a valid number.");
            return;
        }

        String category = categoryInput.getValue();
        if (category == null) {
            showError("Please select a category.");
            return;
        }

        LocalDate localDate = dateInput.getValue();
        if (localDate == null) {
            showError("Please pick a transaction date.");
            return;
        }

        String notes = notesInput.getText();
        if (notes == null || notes.trim().isEmpty()) {
            notes = "No description";
        }

        // Calculation: Expense is stored as a negative number
        double finalAmount = type.equals("Expense") ? -rawAmount : rawAmount;

        // Date format: "Oct 24, 2023"
        String formattedDate = localDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));

        // Save
        Transaction newTransaction = new Transaction(
                formattedDate,
                category,
                type.equals("Expense") ? "Subscription" : "Direct Deposit", // Type details
                notes,
                finalAmount,
                "Completed" // Default state
        );

        TransactionState.getInstance().addTransaction(newTransaction);

        // Reset & Navigate Back
        resetForm();
        if (mainController != null) {
            mainController.navigateToDashboard();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        resetForm();
        if (mainController != null) {
            mainController.navigateToDashboard();
        }
    }

    private void resetForm() {
        typeInput.setValue("Expense");
        amountInput.clear();
        categoryInput.setValue(null);
        dateInput.setValue(LocalDate.now());
        notesInput.clear();
        showError(null);
    }

    private void showError(String msg) {
        if (msg == null) {
            validationErrorLabel.setText("");
            validationErrorLabel.setVisible(false);
            validationErrorLabel.setManaged(false);
        } else {
            validationErrorLabel.setText("⚠  " + msg);
            validationErrorLabel.setVisible(true);
            validationErrorLabel.setManaged(true);
        }
    }
}
