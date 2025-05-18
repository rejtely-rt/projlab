package fungorium.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MainMenuController {

    @FXML
    private VBox layout;

    @FXML
    public void initialize() {
        Button startButton = new Button("Start Game");
        Button testButton = new Button("Test Game");
        Button creditsButton = new Button("Credits");
        Button exitButton = new Button("Exit Game");

        startButton.setMinWidth(200);
        creditsButton.setMinWidth(200);
        exitButton.setMinWidth(200);
        testButton.setMinWidth(200);

        startButton.setOnAction(e -> {
            try {
                NameEntryFlow.start(FungoriumApp.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        testButton.setOnAction(e -> {
            try {
                FungoriumApp.startGameFromNames(
                    List.of("insectist1"),
                    List.of("mycologist1")
                );
                FungoriumApp.initializeForTest();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        creditsButton.setOnAction(e -> {
            try {
                String credits = Files.readString(Paths.get("credits.txt"));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Credits");
                alert.setHeaderText(null);
                alert.setContentText(credits);
                alert.showAndWait();
            } catch (IOException ex) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Could not load credits.");
                errorAlert.setContentText(ex.getMessage());
                errorAlert.showAndWait();
            }
        });

        exitButton.setOnAction(e -> FungoriumApp.getPrimaryStage().close());

        layout.getChildren().addAll(startButton, creditsButton, exitButton);
        layout.getChildren().add(testButton);
    }

    @FXML
    private void onTestButtonClicked() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fungorium/gui/TestSelector.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Teszt kiválasztása");
        stage.setScene(new Scene(root));
        stage.show();
    }
}