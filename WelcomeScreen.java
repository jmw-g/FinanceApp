import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class WelcomeScreen {

    public static void showWelcome(Stage primaryStage, Runnable onContinue) {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #ccffcc;");

        Label welcomeLabel = new Label("Welcome to your Finance App!");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Get today's date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        String formattedDate = today.format(formatter);

        Label dateLabel = new Label(formattedDate);
        dateLabel.setStyle("-fx-font-size: 16px;");

        layout.getChildren().addAll(welcomeLabel, dateLabel);

        Scene scene = new Scene(layout, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

        // After 2 seconds, continue to main app
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> onContinue.run());
        delay.play();
    }
}
