package fungorium.gui;

import fungorium.model.Thread;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import fungorium.model.*;
import fungorium.tectons.*;
import fungorium.spores.*;


//TODO: ezt keresd

public class EntityController {
    
    @FXML
    private Pane canvas;
    
    @FXML
    private HBox playerBox;
    
    private int occupiedPosition = 0;

    private List<Insectist> insectists = new ArrayList<>();
    private List<Mycologist> mycologists = new ArrayList<>();
    
    private ObservableList<EntityViewModel> entities = FXCollections.observableArrayList();
    
    public ObservableList<EntityViewModel> getEntities() {
        return entities;
    }
    
    public void addEntity(EntityViewModel entity) {
        entities.add(entity);
    }
    
    public void refreshController(Map<String, Object> objects) {
        entities.clear();
        occupiedPosition = 0;
        System.out.println("Refreshing controller with objects: " + objects);
        for (Map.Entry<String, Object> entry : objects.entrySet()) {
            Object obj = entry.getValue();
            Class<?> clazz = obj.getClass();
            if (clazz == Insect.class) {
                addEntity(new InsectViewModel((Insect) obj, 0, 0));
            } else if (clazz == Tecton.class) {
                double[] position = getTectonPosition(5, 8, 70, 60);
                addEntity(new TectonViewModel((Tecton) obj, position[0], position[1]));
            } else if (clazz == Thread.class) {
                addEntity(new ThreadViewModel((Thread) obj, 0, 0));
            } else if (clazz == Spore.class) {
                addEntity(new SporeViewModel((Spore) obj, 0, 0));
            } else if (clazz == Mushroom.class) {
                addEntity(new MushroomViewModel((Mushroom) obj, 0, 0));
            }
            // } else if (clazz == Insectist.class) {
            //     addInsectist((Insectist) obj);
            // } else if (clazz == Mycologist.class) {
            //     addMycologist((Mycologist) obj);
            // }
        }
    }
    
    @FXML
    public void initialize() {
        System.out.println("EntityController loaded.");
        // csak canvas layout binding vagy alapbeállítások
        double totalMapWidth = 70 * 4 + 70 / 2; // Példaértékek
        double totalMapHeight = 60 * 4;
        
        Group mapGroup = new Group();
        canvas.getChildren().add(mapGroup);

        mapGroup.layoutXProperty().bind(canvas.widthProperty().divide(2).subtract(totalMapWidth / 2));
        mapGroup.layoutYProperty().bind(canvas.heightProperty().divide(2).subtract(totalMapHeight / 2));

        entities.addListener((ListChangeListener<EntityViewModel>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (EntityViewModel vm : change.getAddedSubList()) {
                        Node view = createViewFor(vm);
                        canvas.getChildren().add(view);
                    }
                }
                if (change.wasRemoved()) {
                    // You could track Node<->VM mapping to remove the correct nodes.
                    // For brevity, we simply clear all and re-add:
                    canvas.getChildren().clear();
                    for (EntityViewModel vm : entities) {
                        canvas.getChildren().add(createViewFor(vm));
                    }
                }
            }
        });
    }

    private Node createViewFor(EntityViewModel vm) {
        Node node = null;
        if (vm instanceof InsectViewModel) {
            InsectViewModel insectVM = (InsectViewModel) vm;
            for (Insectist insectist : insectists) {
                if (insectist.getInsects().contains(insectVM.getModel())) {
                    node = createInsectNode(insectVM, insectist);
                }
            }
        } else if (vm instanceof MushroomViewModel) {
            MushroomViewModel mushVM = (MushroomViewModel) vm;
            for (Mycologist mycologist : mycologists) {
                if (mycologist.getMushrooms().contains(mushVM.getModel())) {
                    node = createMushroomNode(mushVM, mycologist);
                }
            }
        } else if (vm instanceof TectonViewModel) {
            node = createTectonNode((TectonViewModel)vm);
        } else if (vm instanceof ThreadViewModel) {
            node = createThreadNode((ThreadViewModel)vm);
        } else if (vm instanceof SporeViewModel) {
            node = createSporeNode((SporeViewModel)vm);
        }
        return node;
    }

    public void setPlayers(List<Insectist> insectists, List<Mycologist> mycologists) {
        this.insectists = insectists;
        this.mycologists = mycologists;

        // Pálya létrehozása
        Group mapGroup = new Group();
        canvas.getChildren().add(mapGroup);

        // Középre helyezés
        int rows = 4, cols = 4;
        double hexWidth = 70, hexHeight = 60;
        double totalMapWidth = hexWidth * cols + hexWidth / 2;
        double totalMapHeight = hexHeight * rows;

        mapGroup.layoutXProperty().bind(canvas.widthProperty().divide(2).subtract(totalMapWidth / 2));
        mapGroup.layoutYProperty().bind(canvas.heightProperty().divide(2).subtract(totalMapHeight / 2));

        // GUI frissítés
        updatePlayerBox();
    }


    public double[] getTectonPosition(int rows, int cols, double hexWidth, double hexHeight) {
        // Calculate row and column based on position
        int row = occupiedPosition / cols + 1;
        int col = occupiedPosition % cols + 1;

        // Calculate x and y coordinates with hexagonal offset
        double spacing = 10.0; // Spacing between hexagons, adjustable as needed
        double offsetX = (row % 2 == 0) ? 0 : (hexWidth + spacing) / 2; // Offset for odd rows
        double x = col * (hexWidth + spacing) + offsetX;
        double y = row * (hexHeight + spacing);

        occupiedPosition++;

        return new double[] { x, y };
    }

    public void updatePlayerBox() {
        if (mycologists == null || insectists == null) return;
        if (mycologists.isEmpty() && insectists.isEmpty()) return;

        playerBox.getChildren().clear();
        insectists.forEach(i -> playerBox.getChildren().add(createPlayerCard(i)));
        mycologists.forEach(m -> playerBox.getChildren().add(createPlayerCard(m)));
    }

    private VBox createPlayerCard(Object player) {
        String name;
        String role;
        String line1;
        String line2;

        if (player instanceof Insectist i) {
            name = i.getName();
            role = "Insectist";
            line1 = "- insects: " + i.getInsects().size();
            line2 = "- score: " + i.getScore();
        } else if (player instanceof Mycologist m) {
            name = m.getName();
            role = "Mycologist";
            line1 = "- mushrooms: " + m.getMushrooms().size();
            line2 = "- score: " + m.getScore();
        } else {
            name = "Unknown";
            role = "?";
            line1 = "-";
            line2 = "-";
        }

        Label nameLabel = new Label(name + " \n(" + role + ")");
        nameLabel.setStyle("-fx-font-weight: bold;");

        Label info1 = new Label(line1);
        Label info2 = new Label(line2);

        VBox box = new VBox(5, nameLabel, info1, info2);
        box.setStyle("-fx-border-color: red; -fx-padding: 10px;");
        box.setPrefWidth(140);
        box.setPrefHeight(100);
        box.setAlignment(Pos.TOP_CENTER);

        return box;
    }

    private double[] calculateOffset(int index) {
        double radius = 24; // Az eltolás mértéke
        double angle = Math.toRadians(index * 360.0 / 6); // Körkörös elhelyezés
        double offsetX = radius * Math.cos(angle);
        double offsetY = radius * Math.sin(angle);
        return new double[]{offsetX, offsetY};
    }

    private double[] calculateSporeOffset(int index) {
        double radius = 24; // Az eltolás mértéke
        double angle = Math.toRadians(index * 360.0 / 6 - 90); // Körkörös elhelyezés, felülről indulva
        double offsetX = -radius * Math.cos(angle);
        double offsetY = radius * Math.sin(angle);
        return new double[]{offsetX, offsetY};
    }

    public Node createTectonNode(TectonViewModel vm) {
        double centerX = vm.getX();
        double centerY = vm.getY();
        double size = 40; // sugár

        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30);
            double x = centerX + size * Math.cos(angle);
            double y = centerY + size * Math.sin(angle);
            hex.getPoints().addAll(x, y);
        }
        hex.setFill(Color.BEIGE);
        hex.setStroke(Color.DARKGRAY);

        hex.setOnMouseEntered((MouseEvent e) -> {
            Tooltip tooltip = new Tooltip("This tecton is of type: " + vm.getModel().getClass().getSimpleName());
            Tooltip.install(hex, tooltip);
        });

        hex.setOnMouseClicked((MouseEvent e) -> {
            System.out.println("This tecton has been clicked at position: (" + vm.getX() + ", " + vm.getY() + ")");
        });

        return hex;
    }

    public Node createMushroomNode(MushroomViewModel vm, Mycologist player) {
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(
                -10.0, 10.0,
                10.0, 10.0,
                0.0, -10.0
        );
        triangle.setFill(Color.DARKOLIVEGREEN);
        triangle.setStroke(Color.BLACK);

        Group group = new Group(triangle);
        group.layoutXProperty().bind(vm.xProperty());
        group.layoutYProperty().bind(vm.yProperty());

        group.setOnMouseEntered((MouseEvent e) -> {
            Tooltip tooltip = new Tooltip("This mushroom belongs to " + player.getName());
            Tooltip.install(group, tooltip);
        });

        group.setOnMouseClicked((MouseEvent e) -> {
            System.out.println("This mushroom of " + player.getName() + " has been clicked at position: (" + vm.getX() + ", " + vm.getY() + ")");
        });

        return group;
    }

    public Node createInsectNode(InsectViewModel vm, Insectist player) {
        Polygon circle = new Polygon();
        double radius = 10; // sugár
        for (int i = 0; i < 360; i += 10) {
            double angle = Math.toRadians(i);
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            circle.getPoints().addAll(x, y);
        }
        circle.setFill(Color.LIGHTBLUE);
        circle.setStroke(Color.DARKGRAY);

        Group group = new Group(circle);
        group.layoutXProperty().bind(vm.xProperty());
        group.layoutYProperty().bind(vm.yProperty());

        group.setOnMouseEntered((MouseEvent e) -> {
            Tooltip tooltip = new Tooltip("This insect belongs to " + player.getName());
            Tooltip.install(group, tooltip);
        });

        group.setOnMouseClicked((MouseEvent e) -> {
            System.out.println("This insect of " + player.getName() + " has been clicked at position: (" + vm.getX() + ", " + vm.getY() + ")");

            InsectViewModel selectedInsect = vm;

            // Create a one‑shot handler:
            EventHandler<MouseEvent> oneShot = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent te) {
                    // Find the Node under the pointer:
                    Node picked = te.getPickResult().getIntersectedNode();
                    System.out.println("Selected insect: " + selectedInsect.getModel().getClass().getSimpleName());
                    System.out.println("Picked node: " + picked);
                    // Uninstall this handler so it only runs once:
                    group.getScene().removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
                    // Consume to prevent underlying handlers if you like:
                    te.consume();
                }
            };
            group.getScene().addEventHandler(MouseEvent.MOUSE_CLICKED, oneShot);
        });

        return group;
    }

    public Node createSporeNode(SporeViewModel vm) {
        Polygon circle = new Polygon();
        double radius = 5; // sugár
        for (int i = 0; i < 360; i += 10) {
            double angle = Math.toRadians(i);
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            circle.getPoints().addAll(x, y);
        }
        circle.setFill(Color.YELLOW);
        circle.setStroke(Color.DARKGRAY);

        Group group = new Group(circle);
        group.layoutXProperty().bind(vm.xProperty());
        group.layoutYProperty().bind(vm.yProperty());

        group.setOnMouseEntered((MouseEvent e) -> {
            Tooltip tooltip = new Tooltip("This spore is of type: " + vm.getModel().getClass().getSimpleName());
            Tooltip.install(group, tooltip);
        });

        return group;
    }

    public Node createThreadNode(ThreadViewModel vm) {
        Polygon line = new Polygon();
        double length = 15; // hossz
        line.getPoints().addAll(
                -length / 2, 0.0,
                length / 2, 0.0
        );
        line.setFill(Color.BROWN);
        line.setStrokeWidth(5.0);
        line.setStroke(Color.DARKMAGENTA);

        Group group = new Group(line);
        group.layoutXProperty().bind(vm.xProperty());
        group.layoutYProperty().bind(vm.yProperty());

        return group;
    }

    public void refreshViewModels() {
        // későbbi frissítéshez
    }
}