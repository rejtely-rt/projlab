package fungorium.gui;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class EntityController {

    @FXML
    private Pane canvas;

    @FXML
    public void initialize() {
        System.out.println("EntityController initialized. Canvas ready.");
    }
}