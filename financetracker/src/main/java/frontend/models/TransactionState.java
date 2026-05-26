package frontend.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TransactionState {

    private static final TransactionState instance = new TransactionState();
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    private TransactionState() {
        // Preset dummy data to match the mockup design exactly
        transactions.addAll(
                frontend.database.TransactionDAO.getAllTransactions());
    }

    public static TransactionState getInstance() {
        return instance;
    }

    public ObservableList<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction t) {
        // Prepend to show the newest transaction first
        transactions.add(0, t);
    }

    public double getTotalIncome() {
        return transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .filter(a -> a > 0)
                .sum();
    }

    public double getTotalExpense() {
        return Math.abs(transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .filter(a -> a < 0)
                .sum());
    }

    public double getCreditBalance() {
        return transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // Helper to calculate total expense by category for the donut breakdown
    public double getExpenseByCategory(String category) {
        return Math.abs(transactions.stream()
                .filter(t -> t.getAmount() < 0)
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .mapToDouble(Transaction::getAmount)
                .sum());
    }
}
