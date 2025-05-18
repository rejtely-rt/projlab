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
        List<Tecton> tectons = new ArrayList<>();
        for (int i = 0; i < tectonCount; i++) {
            int type = rand.nextInt(4);
            Tecton t;
            switch (type) {
                case 0 -> t = new NoMushTecton();
                case 1 -> t = new SingleThreadTecton();
                case 2 -> t = new ThreadAbsorberTecton();
                case 3 -> t = new ThreadKeeperTecton();
                default -> t = new Tecton();
            }
            tectons.add(t);
        }
    
        // 2. Tectonok összekötése (szomszédok random, de mindenki legalább 1-2 szomszéd)
        for (int i = 0; i < tectonCount; i++) {
            Tecton t1 = tectons.get(i);
            int neighbors = 1 + rand.nextInt(2); // 1 vagy 2 szomszéd
            for (int n = 0; n < neighbors; n++) {
                int j = rand.nextInt(tectonCount);
                if (j != i) {
                    Tecton t2 = tectons.get(j);
                    if (!t1.getNeighbors().contains(t2)) {
                        t1.addNeighbour(t2);
                        t2.addNeighbour(t1);
                    }
                }
            }
        }
    
        // 3. Minden mycologist kap egy gombát egy random tectonra
        for (Mycologist myc : mycologists) {
            Tecton t = tectons.get(rand.nextInt(tectons.size()));
            myc.addMushroom(t);
        }
    
        // 4. Minden insectist kap egy rovart egy random tectonra
        for (Insectist ins : insectists) {
            Tecton t = tectons.get(rand.nextInt(tectons.size()));
            Insect insect = new Insect(ins);
            insect.setLocation(t);
            ins.getInsects().add(insect);
        }
    
        // 5. Véletlenszerűen további gombák
        int extraMushrooms = rand.nextInt(tectonCount / 2);
        for (int i = 0; i < extraMushrooms; i++) {
            Mycologist myc = mycologists.get(rand.nextInt(mycologists.size()));
            Tecton t = tectons.get(rand.nextInt(tectons.size()));
            if (t.getMushroom() == null) {
                myc.addMushroom(t);
            }
        }
    
        // 6. Véletlenszerűen további rovarok
        int extraInsects = rand.nextInt(tectonCount / 2);
        for (int i = 0; i < extraInsects; i++) {
            Insectist ins = insectists.get(rand.nextInt(insectists.size()));
            Tecton t = tectons.get(rand.nextInt(tectons.size()));
            Insect insect = new Insect(ins);
            insect.setLocation(t);
            ins.getInsects().add(insect);
        }
    
        // 7. Minden gomba termeljen random típusú spórákat induláskor
        for (Mycologist myc : mycologists) {
            for (Mushroom m : myc.getMushrooms()) {
                int sporeCount = 1 + rand.nextInt(2); // 1-2 spóra
                for (int s = 0; s < sporeCount; s++) {
                    Spore spore;
                    switch (rand.nextInt(5)) {
                        case 0 -> spore = new CannotCutSpore();
                        case 1 -> spore = new CloneSpore();
                        case 2 -> spore = new ParalyzeSpore();
                        case 3 -> spore = new SlowlySpore();
                        case 4 -> spore = new SpeedySpore();
                        default -> spore = new SlowlySpore();
                    }
                    m.addSpore(spore); // vagy ahogy a modelledben kell
                }
            }
        }
    
        Interpreter.getController().refreshController(Interpreter.getObjects());
        System.out.println("Game initialized with random objects.");
        System.out.println(Interpreter.getObjects());
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