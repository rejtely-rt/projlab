package fungorium.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

import fungorium.model.*;

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

        primaryStage.setTitle("Fungorium - The Game");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}