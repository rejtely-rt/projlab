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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fungorium.model.*;
import fungorium.spores.*;
import fungorium.tectons.*;
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
        initializeGameObjects(insectists, mycologists);
    }


    private static void initializeGameObjects(List<Insectist> insectists, List<Mycologist> mycologists) {
        Random rand = new Random();

        // 1. Tectonok létrehozása különböző típusokkal
        int tectonCount = 10 + rand.nextInt(3); // 10-12 tecton
        List<String> tectonIds = new ArrayList<>();
        for (int i = 0; i < tectonCount; i++) {
            String id = "t" + (i + 1);
            String type;
            switch (rand.nextInt(4)) {
                case 0 -> type = "nomushtecton";
                case 1 -> type = "singlethreadtecton";
                case 2 -> type = "threadabsorbertecton";
                case 3 -> type = "threadkeepertecton";
                default -> type = "tecton";
            }
            Interpreter.executeCommand("/addt -id " + id + " -t " + type);
            tectonIds.add(id);
        }

        // 2. Tectonok összekötése (szomszédok random, de mindenki legalább 1-2 szomszéd)
        for (int i = 0; i < tectonCount; i++) {
            String t1 = tectonIds.get(i);
            int neighbors = 1 + rand.nextInt(2); // 1 vagy 2 szomszéd
            for (int n = 0; n < neighbors; n++) {
                int j = rand.nextInt(tectonCount);
                if (j != i) {
                    String t2 = tectonIds.get(j);
                    Interpreter.executeCommand("/altt -id " + t1 + " -addn " + t2);
                }
            }
        }

        // 3. Minden mycologist kap egy gombát egy random tectonra
        for (Mycologist myc : mycologists) {
            String t = tectonIds.get(rand.nextInt(tectonIds.size()));
            String mId = "m" + myc.getName();
            Interpreter.executeCommand("/addm -id " + mId + " -t " + t + " -my " + myc.getName());
        }

        // 4. Minden insectist kap egy rovart egy random tectonra
        for (Insectist ins : insectists) {
            String t = tectonIds.get(rand.nextInt(tectonIds.size()));
            String iId = "i" + ins.getName();
            Interpreter.executeCommand("/addi -id " + iId + " -t " + t + " -in " + ins.getName());
        }

        // 5. Véletlenszerűen további gombák
        int extraMushrooms = rand.nextInt(tectonCount / 2);
        for (int i = 0; i < extraMushrooms; i++) {
            Mycologist myc = mycologists.get(rand.nextInt(mycologists.size()));
            String t = tectonIds.get(rand.nextInt(tectonIds.size()));
            String mId = "m" + myc.getName() + "_extra" + i;
            Interpreter.executeCommand("/addm -id " + mId + " -t " + t + " -my " + myc.getName());
        }

        // 6. Véletlenszerűen további rovarok
        int extraInsects = rand.nextInt(tectonCount / 2);
        for (int i = 0; i < extraInsects; i++) {
            Insectist ins = insectists.get(rand.nextInt(insectists.size()));
            String t = tectonIds.get(rand.nextInt(tectonIds.size()));
            String iId = "i" + ins.getName() + "_extra" + i;
            Interpreter.executeCommand("/addi -id " + iId + " -t " + t + " -in " + ins.getName());
        }

        // 7. Minden gomba termeljen random típusú spórákat induláskor
        for (Mycologist myc : mycologists) {
            for (Mushroom m : myc.getMushrooms()) {
                String mId = null;
                // Find the mushroom's ID in Interpreter.objectNames
                for (Map.Entry<String, Object> entry : Interpreter.getObjectNames().entrySet()) {
                    if (entry.getValue() == m) {
                        mId = entry.getKey();
                        break;
                    }
                }
                if (mId == null) continue;
                int sporeCount = 1 + rand.nextInt(2); // 1-2 spóra
                for (int s = 0; s < sporeCount; s++) {
                    String sporeType;
                    switch (rand.nextInt(5)) {
                        case 0 -> sporeType = "cannotcutspore";
                        case 1 -> sporeType = "clonespore";
                        case 2 -> sporeType = "paralyzespore";
                        case 3 -> sporeType = "slowyspore";
                        case 4 -> sporeType = "speedyspore";
                        default -> sporeType = "slowyspore";
                    }
                    Interpreter.executeCommand("/addsp -m " + mId + " -tp " + sporeType);
                }
            }
        }

        Interpreter.getController().refreshController(Interpreter.getObjects());
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