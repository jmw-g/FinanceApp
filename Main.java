import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.collections.*;
import java.io.*;
import java.util.*;

public class Main extends Application {

    private ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private TableView<Expense> table = new TableView<>();
    private Label balanceLabel = new Label();
    private Label totalExpensesLabel = new Label();
    private Label savingsGoalLabel = new Label();
    private Label savingsRemainingLabel = new Label();

    private double balance = 1000.00;
    private double totalExpenses = 0.00;
    private double savingsGoal = 0.00;

    private Stage mainStage;
    private String username;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        showLoginScreen(primaryStage);
    }

    private void showLoginScreen(Stage primaryStage) {
        username = Login.showLoginWindow();
        if (username == null) {
            System.exit(0);
        }

        WelcomeScreen.showWelcome(primaryStage, username, () -> {
            mainStage = primaryStage;
            showFinanceApp();
        });
    }

    private void showFinanceApp() {
        mainStage.setTitle("Personal Finance Budget App - " + username);

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        Button addButton = new Button("Add Expense");
        addButton.setStyle("-fx-background-color: #90ee90; -fx-font-weight: bold;");

        Button depositButton = new Button("Deposit Money");
        depositButton.setStyle("-fx-background-color: #ffd700; -fx-font-weight: bold;");

        Button setGoalButton = new Button("Set Savings Goal");
        setGoalButton.setStyle("-fx-background-color: #add8e6; -fx-font-weight: bold;");

        Button saveButton = new Button("Save Expenses");
        saveButton.setStyle("-fx-background-color: #d3d3d3; -fx-font-weight: bold;");

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #ff7f7f; -fx-font-weight: bold;");

        addButton.setOnAction(e -> addExpense(categoryField, amountField));
        depositButton.setOnAction(e -> depositMoney());
        setGoalButton.setOnAction(e -> setSavingsGoal());
        saveButton.setOnAction(e -> saveExpensesToFile());
        logoutButton.setOnAction(e -> logout());

        TableColumn<Expense, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> data.getValue().categoryProperty());

        TableColumn<Expense, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> data.getValue().amountProperty().asObject());

        table.getColumns().addAll(categoryCol, amountCol);
        table.setItems(expenses);

        HBox inputLayout = new HBox(10, categoryField, amountField, addButton, depositButton, setGoalButton, saveButton, logoutButton);
        inputLayout.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(15, balanceLabel, totalExpensesLabel, savingsGoalLabel, savingsRemainingLabel, inputLayout, table);
        mainLayout.setPadding(new Insets(15));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: #ccffcc;");

        updateLabels();
        loadExpensesFromFile();

        mainStage.setScene(new Scene(mainLayout, 1000, 600));
        mainStage.show();
    }

    private void updateLabels() {
        balanceLabel.setText(String.format("Current Balance: $%.2f", balance));
        balanceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        totalExpensesLabel.setText(String.format("Total Expenses: $%.2f", totalExpenses));
        totalExpensesLabel.setStyle("-fx-font-size: 14px;");
        savingsGoalLabel.setText(String.format("Savings Goal: $%.2f", savingsGoal));
        savingsGoalLabel.setStyle("-fx-font-size: 14px;");
        savingsRemainingLabel.setText(String.format("Remaining to Save: $%.2f", Math.max(0, savingsGoal - balance)));
        savingsRemainingLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
    }

    private void addExpense(TextField categoryField, TextField amountField) {
        String category = categoryField.getText();
        String amountText = amountField.getText();

        if (category.isEmpty() || amountText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            if (amount > balance) {
                showAlert(Alert.AlertType.ERROR, "Error", "Not enough balance!");
                return;
            }

            Expense expense = new Expense(category, amount);
            expenses.add(expense);

            balance -= amount;
            totalExpenses += amount;
            updateLabels();

            if (balance < 100) {
                showAlert(Alert.AlertType.WARNING, "Low Balance", "Warning: Your balance is getting low!");
            }

            categoryField.clear();
            amountField.clear();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Amount must be a number.");
        }
    }

    private void depositMoney() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Deposit Money");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter deposit amount:");

        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Amount must be positive.");
                    return;
                }
                balance += amount;
                updateLabels();
                if (balance >= savingsGoal && savingsGoal > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Goal Achieved!", "Congratulations! You've reached your savings goal!");
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid amount entered.");
            }
        });
    }

    private void setSavingsGoal() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Set Savings Goal");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter your savings goal amount:");

        dialog.showAndWait().ifPresent(goalStr -> {
            try {
                double goal = Double.parseDouble(goalStr);
                if (goal <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Goal must be positive.");
                    return;
                }
                savingsGoal = goal;
                updateLabels();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid goal entered.");
            }
        });
    }

    private void logout() {
        expenses.clear();
        balance = 1000.00;
        totalExpenses = 0.00;
        savingsGoal = 0.00;
        username = null;

        showLoginScreen(mainStage);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void saveExpensesToFile() {
        try (PrintWriter writer = new PrintWriter(username + "_expenses.txt")) {
            for (Expense expense : expenses) {
                writer.println(expense.getCategory() + "," + expense.getAmount());
            }
            showAlert(Alert.AlertType.INFORMATION, "Success", "Expenses saved successfully.");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save expenses.");
        }
    }

    private void loadExpensesFromFile() {
        File file = new File(username + "_expenses.txt");
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length == 2) {
                    String category = parts[0];
                    double amount = Double.parseDouble(parts[1]);
                    expenses.add(new Expense(category, amount));
                    balance -= amount;
                    totalExpenses += amount;
                }
            }
            updateLabels();
        } catch (Exception e) {
            System.out.println("Failed to load expenses.");
        }
    }
}
