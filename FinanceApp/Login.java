import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import java.io.*;
import java.util.*;

public class Login {
    private static final String USERS_FILE = "users.txt";

    public static String showLoginWindow() {
        Stage loginStage = new Stage();
        loginStage.setTitle("Login");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Create Account");
        Label messageLabel = new Label();

        VBox layout = new VBox(10, usernameField, passwordField, loginButton, registerButton, messageLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (validateLogin(username, password)) {
                loginStage.setUserData(username);
                loginStage.close();
            } else {
                messageLabel.setText("Invalid credentials. Try again.");
            }
        });

        registerButton.setOnAction(e -> showRegisterWindow());

        Scene scene = new Scene(layout, 300, 250);
        loginStage.setScene(scene);
        loginStage.showAndWait();

        Object user = loginStage.getUserData();
        return user instanceof String ? (String) user : null;
    }

    private static boolean validateLogin(String username, String password) {
        try (Scanner scanner = new Scanner(new File(USERS_FILE))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // file not found yet
        }
        return false;
    }

    private static void showRegisterWindow() {
        Stage regStage = new Stage();
        regStage.setTitle("Create Account");

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        PasswordField confirmField = new PasswordField();
        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");
        confirmField.setPromptText("Confirm Password");

        Button submitButton = new Button("Submit");
        Label messageLabel = new Label();

        VBox layout = new VBox(10, usernameField, passwordField, confirmField, submitButton, messageLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        submitButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirm = confirmField.getText();
            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                messageLabel.setText("Fill in all fields.");
            } else if (!password.equals(confirm)) {
                messageLabel.setText("Passwords do not match.");
            } else {
                if (registerUser(username, password)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Account created!");
                    regStage.close();
                } else {
                    messageLabel.setText("User already exists.");
                }
            }
        });

        Scene scene = new Scene(layout, 300, 250);
        regStage.setScene(scene);
        regStage.showAndWait();
    }

    private static boolean registerUser(String username, String password) {
        try {
            File file = new File(USERS_FILE);
            if (!file.exists()) file.createNewFile();
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String[] parts = scanner.nextLine().split(",");
                    if (parts.length == 2 && parts[0].equals(username)) {
                        return false;
                    }
                }
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
                writer.println(username + "," + password);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
