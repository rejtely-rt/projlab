package fungorium.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import fungorium.model.Mushroom;
import javafx.scene.input.*;
import java.util.List;

public class EntityController {

    @FXML
    private Pane canvas;

    @FXML
    public void initialize() {
        System.out.println("EntityController initialized. Canvas ready.");

        // Középre helyezett gomba
        Mushroom mushroom = new Mushroom(2);
        mushroom.setX(300);
        mushroom.setY(300);
        MushroomViewModel mushroomVM = new MushroomViewModel(mushroom);
        addEntity(mushroomVM);
    }

    public void addEntity(EntityViewModel vm) {
        Node node = createViewFor(vm);
        if (node != null) {
            canvas.getChildren().add(node);
        }
    }

    public Node createViewFor(EntityViewModel vm) {
        if (vm instanceof MushroomViewModel mushroom) {
            return createMushroomNode(mushroom);
        }
        return null;
    }

    public Node createMushroomNode(MushroomViewModel vm) {
        Polygon triangle = new Polygon();
        double x = vm.getX();
        double y = vm.getY();
        triangle.getPoints().addAll(
                x, y - 20.0,
                x - 15.0, y + 15.0,
                x + 15.0, y + 15.0
        );
        triangle.setFill(Color.DARKOLIVEGREEN);
        triangle.setStroke(Color.BLACK);

        triangle.setOnMouseClicked((MouseEvent e) -> {
            System.out.println("This mushroom object has been clicked.");
        });

        return triangle;
    }

    public void refreshViewModels() {
        // későbbi implementációhoz
    }
}