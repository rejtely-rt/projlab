package fungorium.gui;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import fungorium.model.*;
import fungorium.utils.Interpreter;

public class FungoriumApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showMainMenu();
    }

    public static void showMainMenu() throws Exception {
        FXMLLoader loader = new FXMLLoader(FungoriumApp.class.getResource("/fungorium/gui/MainMenu.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Fungorium - Main Menu");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void startGameFromNames(List<String> insectistNames, List<String> mycologistNames) throws Exception {
        List<Insectist> insectists = insectistNames.stream()
                .map(Insectist::new)
                .toList();

        List<Mycologist> mycologists = mycologistNames.stream()
                .map(Mycologist::new)
                .toList();

        showGameView(insectists, mycologists);
    }

    public static void showGameView(List<Insectist> insectists, List<Mycologist> mycologists) throws Exception {
        FXMLLoader loader = new FXMLLoader(FungoriumApp.class.getResource("/fungorium/gui/EntityView.fxml"));
        Parent root = loader.load();

        EntityController controller = loader.getController();
        controller.setPlayers(insectists, mycologists);

        Interpreter.setController(controller);
        Interpreter.setMycologists(mycologists); // <-- EZT ADD HOZZÁ

        primaryStage.setTitle("Fungorium - The Game");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        //initializeGameObjects();
    }


    private static void initializeGameObjects() {
        for (int i = 1; i <= 30; i++) {
            Interpreter.executeCommand("/addt -id T" + i + " -t Tecton");
        }
    }

    public static void initializeForTest() {
        for (int i = 1; i <= 10; i++) {
            Interpreter.executeCommand("/addt -id T" + i + " -t Tecton");
        }
    }
    
    public static void runTestFile(String filename, EntityController controller) throws IOException {
        List<String> commands = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.trim().startsWith("//")) {
                    commands.add(line.trim());
                }
            }
        }
        runTestFileAnimated(commands, controller, 0);
    }

    private static void runTestFileAnimated(List<String> commands, EntityController controller, int index) {
        if (index >= commands.size()) return;
        Interpreter.executeCommand(commands.get(index));
        PauseTransition pause = new PauseTransition(Duration.millis(400));
        pause.setOnFinished(e -> runTestFileAnimated(commands, controller, index + 1));
        pause.play();
    }
    
    public static void main(String[] args) {
        launch();
    }
}