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
        Button registerButton = new Button("Register");
        Label messageLabel = new Label();

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(usernameField, passwordField, loginButton, registerButton, messageLabel);

        Scene scene = new Scene(layout, 300, 250);
        loginStage.setScene(scene);

        final String[] loggedInUser = {null};

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (validateLogin(username, password)) {
                loggedInUser[0] = username;
                loginStage.close();
            } else {
                messageLabel.setText("Invalid credentials. Try again.");
            }
        });

        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Fill in both fields.");
            } else {
                if (registerUser(username, password)) {
                    showSuccessPopup("Registered successfully! Now login.");
                } else {
                    messageLabel.setText("User already exists.");
                }
            }
        });

        loginStage.showAndWait();
        return loggedInUser[0];
    }

    private static boolean validateLogin(String username, String password) {
        try (Scanner scanner = new Scanner(new File(USERS_FILE))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            // No users yet
        }
        return false;
    }

    private static boolean registerUser(String username, String password) {
        try {
            File file = new File(USERS_FILE);
            if (!file.exists()) {
                file.createNewFile();
            }
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

    private static void showSuccessPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
