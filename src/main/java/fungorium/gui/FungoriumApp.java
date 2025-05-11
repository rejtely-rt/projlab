package fungorium.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

    public static void showGameView() throws Exception {
        FXMLLoader loader = new FXMLLoader(FungoriumApp.class.getResource("/fungorium/gui/EntityView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Fungorium - Game View");
        primaryStage.setScene(new Scene(root));
    }

    public static void main(String[] args) {
        launch();
    }
}