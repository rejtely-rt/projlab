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
        Interpreter.setMycologists(mycologists);
        Interpreter.setInsectists(insectists);
        primaryStage.setTitle("Fungorium - The Game");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        initializeGameObjects(insectists, mycologists);
    }


    private static void initializeGameObjects(List<Insectist> insectists, List<Mycologist> mycologists) {
        Random rand = new Random();

        // 1. Játékosok regisztrálása Interpreter-ben
        for (Mycologist myc : mycologists) {
            Interpreter.getObjectNames().put(myc.getName(), myc);
        }
        for (Insectist ins : insectists) {
            Interpreter.getObjectNames().put(ins.getName(), ins);
        }

        // 2. Tectonok létrehozása Interpreter parancsokkal
        int tectonCount = 10 + rand.nextInt(3);
        List<String> tectonNames = new ArrayList<>();
        for (int i = 0; i < tectonCount; i++) {
            String id = "t" + i;
            String type;
            switch (rand.nextInt(4)) {
                case 0 -> type = "nomushtecton";
                case 1 -> type = "singlethreadtecton";
                case 2 -> type = "threadabsorbertecton";
                case 3 -> type = "threadkeepertecton";
                default -> type = "tecton";
            }
            Interpreter.executeCommand("addt -id " + id + " -t " + type);
            tectonNames.add(id);
        }

        // 3. Tectonok összekötése (szomszédok random, Interpreter-rel)
        for (int i = 0; i < tectonCount; i++) {
            int neighbors = 1 + rand.nextInt(2);
            for (int n = 0; n < neighbors; n++) {
                int j = rand.nextInt(tectonCount);
                if (j != i) {
                    Interpreter.executeCommand("altt -id " + tectonNames.get(i) + " -addn " + tectonNames.get(j));
                }
            }
        }

        // 4. Minden mycologist kap egy gombát egy random tectonra
        for (Mycologist myc : mycologists) {
            String tName = tectonNames.get(rand.nextInt(tectonNames.size()));
            String mName = "m_" + myc.getName();
            Interpreter.executeCommand("addm -id " + mName + " -t " + tName + " -my " + myc.getName() + " -lvl 1");
        }

        // 5. Minden insectist kap egy rovart egy random tectonra
        for (Insectist ins : insectists) {
            String tName = tectonNames.get(rand.nextInt(tectonNames.size()));
            String iName = "i_" + ins.getName();
            Interpreter.executeCommand("addi -id " + iName + " -t " + tName + " -in " + ins.getName());
        }

        // 6. Véletlenszerűen további gombák
        int extraMushrooms = rand.nextInt(tectonCount / 2);
        for (int i = 0; i < extraMushrooms; i++) {
            Mycologist myc = mycologists.get(rand.nextInt(mycologists.size()));
            String tName = tectonNames.get(rand.nextInt(tectonNames.size()));
            String mName = "m_extra" + i;
            Interpreter.executeCommand("addm -id " + mName + " -t " + tName + " -my " + myc.getName() + " -lvl 1");
        }

        // 7. Véletlenszerűen további rovarok
        int extraInsects = rand.nextInt(tectonCount / 2);
        for (int i = 0; i < extraInsects; i++) {
            Insectist ins = insectists.get(rand.nextInt(insectists.size()));
            String tName = tectonNames.get(rand.nextInt(tectonNames.size()));
            String iName = "i_extra" + i;
            Interpreter.executeCommand("addi -id " + iName + " -t " + tName + " -in " + ins.getName());
        }

        // 8. Minden gomba termeljen random típusú spórákat induláskor
        for (Mycologist myc : mycologists) {
            for (Mushroom m : myc.getMushrooms()) {
                int sporeCount = 1 + rand.nextInt(2);
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
                    // Az Interpreter-ben a mushroom neve a getObjectNames() alapján lehet "_auto" végű is!
                    String mName = null;
                    for (Map.Entry<String, Object> entry : Interpreter.getObjectNames().entrySet()) {
                        if (entry.getValue() == m) {
                            mName = entry.getKey();
                            break;
                        }
                    }
                    if (mName != null) {
                        Interpreter.executeCommand("addsp -m " + mName + " -tp " + sporeType);
                    }
                }
            }
        }

        Interpreter.getController().refreshController(Interpreter.getObjects());
        System.out.println("Game initialized with random objects (Interpreter commands).");
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