import javafx.beans.property.*;

public class Expense {

    private final StringProperty category;
    private final DoubleProperty amount;

    public Expense(String category, double amount) {
        this.category = new SimpleStringProperty(category);
        this.amount = new SimpleDoubleProperty(amount);
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public double getAmount() {
        return amount.get();
    }

    public void setAmount(double amount) {
        this.amount.set(amount);
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public DoubleProperty amountProperty() {
        return amount;
    }
}
