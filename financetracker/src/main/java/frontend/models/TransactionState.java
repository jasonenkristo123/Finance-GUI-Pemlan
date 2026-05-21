package frontend.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TransactionState {

    private static final TransactionState instance = new TransactionState();
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    private TransactionState() {
        // Preset dummy data to match the mockup design exactly
        transactions.addAll(
            new Transaction("Oct 24, 2023", "Salary", "Direct Deposit", "Salary Deposit", 8500.00, "Completed"),
            new Transaction("Oct 23, 2023", "Housing", "Wire Transfer", "Rent Payment", -1200.00, "Completed"),
            new Transaction("Oct 22, 2023", "Food & Dining", "Debit Card", "Grocery Market", -245.50, "Pending"),
            new Transaction("Oct 21, 2023", "Transport", "Credit Card", "Gas Station", -45.00, "Completed"),
            new Transaction("Oct 18, 2023", "Revenue", "Direct Deposit", "Freelance Design", 6700.00, "Completed"),
            new Transaction("Oct 15, 2023", "Transport", "Wire Transfer", "Flight Ticket", -340.00, "Completed"),
            new Transaction("Oct 12, 2023", "Housing", "Auto-Draft", "Electricity & Water Bills", -395.00, "Completed"),
            new Transaction("Oct 10, 2023", "Food & Dining", "Debit Card", "Restaurant Dinner", -249.50, "Completed"),
            new Transaction("Oct 08, 2023", "Other", "Subscription", "Gym Membership & Streaming", -275.00, "Completed")
        );
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
