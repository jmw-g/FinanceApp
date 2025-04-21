import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WelcomeScreen {
    public static void showWelcome(Stage primaryStage, Runnable onContinue) {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #ccffcc;");

        Label welcome = new Label("Welcome to the Finance App!");
        welcome.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        Label dateLabel = new Label(today);
        dateLabel.setStyle("-fx-font-size: 16px;");

        layout.getChildren().addAll(welcome, dateLabel);

        Scene scene = new Scene(layout, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> onContinue.run());
        delay.play();
    }
}
