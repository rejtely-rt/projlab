package fungorium.gui;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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

//TODO: ezt keresd

public class EntityController {

    @FXML
    private Pane canvas;

    @FXML
    private HBox playerBox;

    private List<Insectist> insectists = new ArrayList<>();
    private List<Mycologist> mycologists = new ArrayList<>();
    List<TectonViewModel> tectons = new ArrayList<>();

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

        // Tektonok generálása
        tectons = generateTectonGrid(mapGroup, rows, cols, hexWidth, hexHeight);

        // Gombák lerakása
        placeStartingMushrooms(mapGroup);

        // Rovarok lerakása
        placeStartingInsects(mapGroup);

        // GUI frissítés
        updatePlayerBox();
    }

    private List<TectonViewModel> generateTectonGrid(Group mapGroup, int rows, int cols, double hexWidth, double hexHeight) {
        List<TectonViewModel> tectons = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double spacing = 10; // Távolság a hatszögek között
                double offsetX = (row % 2 == 0) ? 0 : (hexWidth + spacing) / 2;
                double x = col * (hexWidth + spacing) + offsetX;
                double y = row * (hexHeight + spacing);
                Tecton model = createRandomTecton();
                TectonViewModel vm = new TectonViewModel(model, x, y);
                tectons.add(vm);
                Node hexNode = createTectonNode(vm);
                mapGroup.getChildren().add(hexNode);
            }
        }

        return tectons;
    }


    private Tecton createRandomTecton() {
        List<Supplier<Tecton>> tectonTypes = List.of(
                NoMushTecton::new,
                SingleThreadTecton::new,
                ThreadAbsorberTecton::new,
                ThreadKeeperTecton::new,
                Tecton::new
        );
        return tectonTypes.get(new Random().nextInt(tectonTypes.size())).get();
    }

    private void placeStartingMushrooms(Group mapGroup) {
        if (mycologists == null || mycologists.isEmpty()) return;

        List<TectonViewModel> shuffledTectons = tectons.stream()
                .filter(t -> !(t.getModel() instanceof NoMushTecton))
                .collect(Collectors.toList());
        Collections.shuffle(shuffledTectons);

        for (Mycologist player : mycologists) {
            for (int i = 0; i < 4; i++) { // Adjust the number of mushrooms per player as needed
                player.getMushrooms().add(new Mushroom(1));
            }

            List<Mushroom> mushrooms = player.getMushrooms();
            for (int i = 0; i < Math.min(mushrooms.size(), shuffledTectons.size()); i++) {
                TectonViewModel tecton = shuffledTectons.remove(0);
                Mushroom mushModel = mushrooms.get(i);
                tecton.getModel().addMushroom(mushModel);

                MushroomViewModel mushVM = new MushroomViewModel(mushModel, 0, 0);
                tecton.addEntity(mushVM); // Add the mushroom to the Tecton's entities

                // Bind the mushroom's position directly to the Tecton's center
                mushVM.xProperty().bind(tecton.xProperty());
                mushVM.yProperty().bind(tecton.yProperty());

                Node mushNode = createMushroomNode(mushVM, player);
                mapGroup.getChildren().add(mushNode);
            }

            player.setScore(player.getMushrooms().size());
        }
    }

    private void placeStartingInsects(Group macGroup) {
        if(insectists == null || insectists.isEmpty()) return;

        //nem kell filter a NoMushTecton miatt mert barmelyik tectonra rakhatunk
        List<TectonViewModel> shuffledTectons = tectons.stream()
                .collect(Collectors.toList());
        Collections.shuffle(shuffledTectons);

        //helyezzunk le minden jatekoshoz 2 rovarat a gombahoz hasonloan
        for(Insectist player: insectists) {
            for(int i = 0; i < 4; i++) { // Adjust the number of insects per player as needed
                player.getInsects().add(new Insect());
            }

            List<Insect> insects = player.getInsects();
            for (int i = 0; i < Math.min(insects.size(), shuffledTectons.size()); i++) {
                TectonViewModel tecton = shuffledTectons.remove(0);
                Insect insectModel = insects.get(i);

                InsectViewModel insectVM = new InsectViewModel(insectModel, 0, 0);
                tecton.addEntity(insectVM); // Hozzáadjuk a Tecton entitásaihoz

                double[] offset = calculateOffset(tecton.getEntities().size() - 1);
                insectVM.xProperty().bind(tecton.xProperty().add(offset[0]));
                insectVM.yProperty().bind(tecton.yProperty().add(offset[1]));

                Node insectNode = createInsectNode(insectVM, player);
                macGroup.getChildren().add(insectNode);
            }
            player.setScore(player.getInsects().size());
        }
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
        });

        return group;
    }

    public void refreshViewModels() {
        // későbbi frissítéshez
    }
}