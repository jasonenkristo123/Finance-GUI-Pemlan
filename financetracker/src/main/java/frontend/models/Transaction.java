package frontend.models;

import javafx.beans.property.*;

public class Transaction {

    private final StringProperty date;
    private final StringProperty category;
    private final StringProperty type;
    private final StringProperty notes;
    private final DoubleProperty amount;

    public Transaction(String date, String category, String type, String notes, double amount) {
        this.date = new SimpleStringProperty(date);
        this.category = new SimpleStringProperty(category);
        this.type = new SimpleStringProperty(type);
        this.notes = new SimpleStringProperty(notes);
        this.amount = new SimpleDoubleProperty(amount);
    }

    // Property accessors (needed by TableView)
    public StringProperty dateProperty() { return date; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty typeProperty() { return type; }
    public StringProperty notesProperty() { return notes; }
    public DoubleProperty amountProperty() { return amount; }

    // Standard getters
    public String getDate() { return date.get(); }
    public String getCategory() { return category.get(); }
    public String getType() { return type.get(); }
    public String getNotes() { return notes.get(); }
    public double getAmount() { return amount.get(); }
}
