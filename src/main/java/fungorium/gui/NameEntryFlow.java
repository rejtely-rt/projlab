package fungorium.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import fungorium.model.Insectist;
import fungorium.model.Mushroom;
import fungorium.model.Mycologist;


public class NameEntryFlow {

    private static int insectistCount = 1;
    private static int mycologistCount = 1;
    private static final List<String> insectistNames = new ArrayList<>();
    private static final List<String> mycologistNames = new ArrayList<>();
    private static int currentInsectistIndex = 0;
    private static int currentMycologistIndex = 0;

    public static void start(Stage stage) {
        showPlayerCountScreen(stage);
    }

    private static void showPlayerCountScreen(Stage stage) {
        Label title = new Label("New Game");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: navy;");

        Label mycoLabel = new Label("Mycologist Players:");
        Label mycoCount = new Label("1");
        Button mycoPlus = new Button("+");
        Button mycoMinus = new Button("-");
        mycoPlus.setOnAction(e -> mycoCount.setText(String.valueOf(Integer.parseInt(mycoCount.getText()) + 1)));
        mycoMinus.setOnAction(e -> {
            int current = Integer.parseInt(mycoCount.getText());
            if (current > 1) mycoCount.setText(String.valueOf(current - 1));
        });

        HBox mycoRow = new HBox(10, mycoLabel, mycoMinus, mycoCount, mycoPlus);
        mycoRow.setAlignment(Pos.CENTER);

        Label insectLabel = new Label("Insectist Players:");
        Label insectCount = new Label("1");
        Button insectPlus = new Button("+");
        Button insectMinus = new Button("-");
        insectPlus.setOnAction(e -> insectCount.setText(String.valueOf(Integer.parseInt(insectCount.getText()) + 1)));
        insectMinus.setOnAction(e -> {
            int current = Integer.parseInt(insectCount.getText());
            if (current > 1) insectCount.setText(String.valueOf(current - 1));
        });

        HBox insectRow = new HBox(10, insectLabel, insectMinus, insectCount, insectPlus);
        insectRow.setAlignment(Pos.CENTER);

        Button doneButton = new Button("Done");
        doneButton.setOnAction(e -> {
            insectistCount = Integer.parseInt(insectCount.getText());
            mycologistCount = Integer.parseInt(mycoCount.getText());
            insectistNames.clear();
            mycologistNames.clear();
            currentInsectistIndex = 0;
            currentMycologistIndex = 0;
            showNextNamePrompt(stage);
        });

        VBox layout = new VBox(20, title, mycoRow, insectRow, doneButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 450, 400);
        stage.setScene(scene);
    }

    private static void showNextNamePrompt(Stage stage) {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);

        Label roleLabel = new Label();
        if (currentInsectistIndex < insectistCount) {
            roleLabel.setText("Insectist " + (currentInsectistIndex + 1));
        } else {
            roleLabel.setText("Mycologist " + (currentMycologistIndex + 1));
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
                showNextNamePrompt(stage);
            } else {
                showSummary(stage);
            }
        });

        layout.getChildren().addAll(roleLabel, nameLabel, nameField, doneButton);
        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
    }

    private static void showSummary(Stage stage) {
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

        try {
            // csak neveket továbbítunk — példányosítás később
            FungoriumApp.startGameFromNames(insectistNames, mycologistNames);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}