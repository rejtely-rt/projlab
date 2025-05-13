package fungorium.gui;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import fungorium.model.Mushroom;

public class EntityController {

    @FXML
    private Pane canvas;

    @FXML
    public void initialize() {
        System.out.println("EntityController initialized. Canvas ready.");

        // Létrehozunk egy MushroomViewModel példányt a középpontra
        Mushroom model = new Mushroom(2);
        MushroomViewModel vm = new MushroomViewModel(model, 400, 300);
        addEntity(vm);
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
        triangle.getPoints().addAll(
                0.0, -20.0,
                -15.0, 15.0,
                15.0, 15.0
        );
        triangle.setFill(Color.DARKOLIVEGREEN);
        triangle.setStroke(Color.BLACK);

        Group group = new Group(triangle);
        group.layoutXProperty().bind(vm.xProperty());
        group.layoutYProperty().bind(vm.yProperty());

        group.setOnMouseClicked((MouseEvent e) -> {
            System.out.println("This mushroom object has been clicked.");
        });

        return group;
    }

    public void refreshViewModels() {
        // későbbi frissítéshez
    }
}