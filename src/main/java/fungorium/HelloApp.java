package fungorium;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HelloApp extends Application {

    private int insectistCount = 1;
    private int mycologistCount = 1;
    private final List<String> insectistNames = new ArrayList<>();
    private final List<String> mycologistNames = new ArrayList<>();
    private int currentInsectistIndex = 0;
    private int currentMycologistIndex = 0;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        showMainMenu();
    }

    private void showMainMenu() {
        Label title = new Label("Fungorium");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button startButton = new Button("Start Game");
        Button creditsButton = new Button("Credits");
        Button exitButton = new Button("Exit Game");

        startButton.setMinWidth(200);
        creditsButton.setMinWidth(200);
        exitButton.setMinWidth(200);

        startButton.setOnAction(e -> showNewGameScreen());

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

        exitButton.setOnAction(e -> primaryStage.close());

        VBox layout = new VBox(20, title, startButton, creditsButton, exitButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setTitle("Fungorium - Main Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showNewGameScreen() {
        Label title = new Label("New Game");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: navy;");

        // Mycologist
        Label mycoLabel = new Label("Mycologist Players:");
        Label mycoCount = new Label("1");
        Button mycoPlus = new Button("+");
        Button mycoMinus = new Button("-");
        mycoPlus.setOnAction(e -> {
            int current = Integer.parseInt(mycoCount.getText());
            mycoCount.setText(String.valueOf(current + 1));
        });
        mycoMinus.setOnAction(e -> {
            int current = Integer.parseInt(mycoCount.getText());
            if (current > 1) mycoCount.setText(String.valueOf(current - 1));
        });

        HBox mycoRow = new HBox(10, mycoLabel, mycoMinus, mycoCount, mycoPlus);
        mycoRow.setAlignment(Pos.CENTER);

        // Insectist
        Label insectLabel = new Label("Insectist Players:");
        Label insectCount = new Label("1");
        Button insectPlus = new Button("+");
        Button insectMinus = new Button("-");
        insectPlus.setOnAction(e -> {
            int current = Integer.parseInt(insectCount.getText());
            insectCount.setText(String.valueOf(current + 1));
        });
        insectMinus.setOnAction(e -> {
            int current = Integer.parseInt(insectCount.getText());
            if (current > 1) insectCount.setText(String.valueOf(current - 1));
        });

        HBox insectRow = new HBox(10, insectLabel, insectMinus, insectCount, insectPlus);
        insectRow.setAlignment(Pos.CENTER);

        // Done gomb
        Button doneButton = new Button("Done");
        doneButton.setOnAction(e -> {
            this.insectistCount = Integer.parseInt(insectCount.getText());
            this.mycologistCount = Integer.parseInt(mycoCount.getText());
            insectistNames.clear();
            mycologistNames.clear();
            currentInsectistIndex = 0;
            currentMycologistIndex = 0;
            showNextNamePrompt();
        });

        VBox layout = new VBox(20, title, mycoRow, insectRow, doneButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 450, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Fungorium - New Game Setup");
    }

    private void showNextNamePrompt() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);

        Label roleLabel;
        if (currentInsectistIndex < insectistCount) {
            roleLabel = new Label("Insectist " + (currentInsectistIndex + 1));
        } else {
            roleLabel = new Label("Mycologist " + (currentMycologistIndex + 1));
        }
        roleLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: navy;");

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Button doneButton = new Button("Done");
        doneButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) return;

            if (currentInsectistIndex < insectistCount) {
                insectistNames.add(name);
                currentInsectistIndex++;
            } else {
                mycologistNames.add(name);
                currentMycologistIndex++;
            }

            if (currentInsectistIndex < insectistCount || currentMycologistIndex < mycologistCount) {
                showNextNamePrompt();
            } else {
                showSummary();
            }
        });

        layout.getChildren().addAll(roleLabel, nameLabel, nameField, doneButton);
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
    }

    private void showSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Insectists:\n");
        insectistNames.forEach(name -> summary.append("- ").append(name).append("\n"));
        summary.append("\nMycologists:\n");
        mycologistNames.forEach(name -> summary.append("- ").append(name).append("\n"));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Players Ready");
        alert.setHeaderText("Game setup complete!");
        alert.setContentText(summary.toString());
        alert.showAndWait();

        // Itt jöhetne: showGameScreen(); vagy játék elindítása
        startGame();
    }

    private void startGame() {
        Label title = new Label("Fungorium Game");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label insectLabel = new Label("Insectist Players:");
        VBox insectList = new VBox(5);
        insectistNames.forEach(name -> {
            Label lbl = new Label(name);
            lbl.setStyle("-fx-background-color: lightyellow; -fx-padding: 5px;");
            insectList.getChildren().add(lbl);
        });

        Label mycoLabel = new Label("Mycologist Players:");
        VBox mycoList = new VBox(5);
        mycologistNames.forEach(name -> {
            Label lbl = new Label(name);
            lbl.setStyle("-fx-background-color: lightblue; -fx-padding: 5px;");
            mycoList.getChildren().add(lbl);
        });

        VBox layout = new VBox(20, title, insectLabel, insectList, mycoLabel, mycoList);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");

        Scene scene = new Scene(layout, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Fungorium - Game View");
    }


    public static void main(String[] args) {
        launch();
    }
}
