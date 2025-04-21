import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.collections.*;
import javafx.collections.ListChangeListener;
import javafx.scene.chart.PieChart;
import java.io.*;
import java.util.*;

public class Main extends Application {
    private ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private TableView<Expense> table = new TableView<>();
    private Label balanceLabel = new Label();
    private Label totalExpensesLabel = new Label();
    private Label savingsGoalLabel = new Label();
    private Label savingsRemainingLabel = new Label();
    private PieChart pieChart = new PieChart();

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
        username = Login.showLoginWindow();
        if (username == null) System.exit(0);

        mainStage = primaryStage;
        WelcomeScreen.showWelcome(primaryStage, this::showFinanceApp);
    }

    private void showFinanceApp() {
        mainStage.setTitle("Personal Finance Budget App - " + username);

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        Button addButton     = new Button("Add Expense");
        Button depositButton = new Button("Deposit Money");
        Button setGoalButton = new Button("Set Savings Goal");
        Button saveButton    = new Button("Save Expenses");
        Button logoutButton  = new Button("Logout");

        addButton.setOnAction(e -> addExpense(categoryField, amountField));
        depositButton.setOnAction(e -> depositMoney());
        setGoalButton.setOnAction(e -> setSavingsGoal());
        saveButton.setOnAction(e -> saveExpensesToFile());
        logoutButton.setOnAction(e -> logout());

        TableColumn<Expense, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(d -> d.getValue().categoryProperty());

        TableColumn<Expense, Double> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(d -> d.getValue().amountProperty().asObject());

        table.getColumns().addAll(catCol, amtCol);
        table.setItems(expenses);

        expenses.addListener((ListChangeListener<Expense>) c -> updatePieChart());

        HBox controls = new HBox(10, categoryField, amountField,
                                 addButton, depositButton, setGoalButton, saveButton, logoutButton);
        controls.setAlignment(Pos.CENTER);

        pieChart.setTitle("Spending Breakdown");
        pieChart.setPrefHeight(300);

        VBox root = new VBox(15,
            balanceLabel, totalExpensesLabel, savingsGoalLabel, savingsRemainingLabel,
            controls, table, pieChart
        );
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.CENTER);

        updateLabels();
        updatePieChart();
        loadExpensesFromFile();

        mainStage.setScene(new Scene(root, 1000, 700));
        mainStage.show();
    }

    private void addExpense(TextField cf, TextField af) {
        String cat = cf.getText(), amtStr = af.getText();
        if (cat.isEmpty() || amtStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }
        try {
            double amt = Double.parseDouble(amtStr);
            if (amt > balance) throw new IllegalArgumentException("Not enough balance!");
            expenses.add(new Expense(cat, amt));
            balance -= amt;
            totalExpenses += amt;
            updateLabels();
            updatePieChart();
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
        }
    }

    private void depositMoney() {
        TextInputDialog dlg = new TextInputDialog();
        dlg.initOwner(mainStage);
        dlg.setTitle("Deposit Money");
        dlg.setHeaderText(null);
        dlg.setContentText("Enter deposit amount:");

        dlg.showAndWait().ifPresent(input -> {
            try {
                double amt = Double.parseDouble(input);
                if (amt <= 0) throw new IllegalArgumentException("Amount must be positive.");
                balance += amt;
                updateLabels();
                updatePieChart();
                if (savingsGoal > 0 && balance >= savingsGoal) {
                    showAlert(Alert.AlertType.INFORMATION, "Goal Achieved!",
                              "Congratulations! You've reached your savings goal!");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });
    }

    private void setSavingsGoal() {
        TextInputDialog dlg = new TextInputDialog();
        dlg.initOwner(mainStage);
        dlg.setTitle("Set Savings Goal");
        dlg.setHeaderText(null);
        dlg.setContentText("Enter your savings goal amount:");

        dlg.showAndWait().ifPresent(input -> {
            try {
                double goal = Double.parseDouble(input);
                if (goal <= 0) throw new IllegalArgumentException("Goal must be positive.");
                savingsGoal = goal;
                updateLabels();
                updatePieChart();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });
    }

    private void saveExpensesToFile() {
        File file = new File(username + "_expenses.txt");
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            for (Expense exp : expenses) {
                out.println(exp.getCategory() + "," + exp.getAmount());
            }
            showAlert(Alert.AlertType.INFORMATION, "Success", "Expenses saved successfully.");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save expenses.");
        }
    }

    private void logout() {
        expenses.clear();
        balance = 1000.00;
        totalExpenses = 0.00;
        savingsGoal = 0.00;
        updateLabels();
        updatePieChart();

        username = null;
        mainStage.hide();
        start(new Stage());
    }

    private void loadExpensesFromFile() {
        File file = new File(username + "_expenses.txt");
        if (!file.exists()) return;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split(",");
                if (p.length == 2) {
                    double amt = Double.parseDouble(p[1]);
                    expenses.add(new Expense(p[0], amt));
                    balance -= amt;
                    totalExpenses += amt;
                }
            }
            updateLabels();
            updatePieChart();
        } catch (Exception e) {
            System.err.println("Load failed: " + e);
        }
    }

    private void updateLabels() {
        balanceLabel.setText(String.format("Balance: $%.2f", balance));
        totalExpensesLabel.setText(String.format("Total Expenses: $%.2f", totalExpenses));
        savingsGoalLabel.setText(String.format("Savings Goal: $%.2f", savingsGoal));
        savingsRemainingLabel.setText(String.format("Remaining to Save: $%.2f", Math.max(0, savingsGoal - balance)));
    }

    private void updatePieChart() {
        Map<String, Double> sumByCat = new HashMap<>();
        for (Expense e : expenses)
            sumByCat.merge(e.getCategory(), e.getAmount(), Double::sum);

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        sumByCat.forEach((c, v) -> data.add(new PieChart.Data(c, v)));
        pieChart.setData(data);
    }

    private void showAlert(Alert.AlertType type, String t, String msg) {
        Alert a = new Alert(type);
        a.initOwner(mainStage);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
