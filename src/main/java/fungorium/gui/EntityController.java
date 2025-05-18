package fungorium.gui;

import fungorium.model.Thread;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import fungorium.model.*;
import fungorium.tectons.*;
import fungorium.utils.Interpreter;
import fungorium.utils.TectonPositioner;
import fungorium.spores.*;


//TODO: ezt keresd

public class EntityController {
    public static EntityController instance; // <-- statikus példány
    
    @FXML
    private Pane canvas;
    
    @FXML
    private HBox playerBox;
    
    @FXML
    private Button sporeButton;
    @FXML
    private Button growThreadButton;
    @FXML
    private Button growMushroomButton;
    @FXML
    private Button endTurnButton;
    @FXML
    private Button cutThreadButton;
    @FXML
    private Button consumeSporeButton;
    @FXML
    private Button moveInsectButton;

    @FXML
    private VBox infoPanel;

    @FXML
    private ScrollPane infoScrollPane;

    @FXML
    private Label turnLabel;

    private MushroomViewModel selectedMushroom = null;
    private boolean sporeShootMode = false;
    private boolean growThreadMode = false;
    private boolean growMushroomMode = false;

    private InsectViewModel selectedInsect = null;
    private boolean moveInsectMode = false;
    private boolean cutThreadMode = false;
    private boolean consumeSporeMode = false;
    
    
    private List<Insectist> insectists = new ArrayList<>();
    private List<Mycologist> mycologists = new ArrayList<>();
    
    private ObservableList<EntityViewModel> entities = FXCollections.observableArrayList();

    private Map<Tecton, TectonViewModel> tectonVMs = new HashMap<>();
    private Map<TectonViewModel, Polygon> tectonNodes = new HashMap<>();
    
    public ObservableList<EntityViewModel> getEntities() {
        return entities;
    }
    
    public void addEntity(EntityViewModel entity) {
        entities.add(entity);
    }
    
    public void refreshController(Map<String, Object> objects) {
        entities.clear();

        TectonPositioner tectonPositioner = new TectonPositioner(this);
        tectonVMs = tectonPositioner.createTectonViewModels(objects, 5, 6, 70, 60);

        // Insectek – csak egyszer végigmenni rajtuk!
        Map<Tecton, Integer> insectPositions = new HashMap<>();
        for (Map.Entry<String, Object> entry : objects.entrySet()) {
            if (entry.getValue() instanceof Insect insect) {
                Tecton tecton = insect.getLocation();
                TectonViewModel tVM = tectonVMs.get(tecton);
                if (tVM != null) {
                    // Ellenőrizd, hogy már van-e rovar ezen a tectonon
                    if (insectPositions.containsKey(tecton)) {
                        // Ha van, akkor csak növeljük az indexet
                        insectPositions.put(tecton, insectPositions.get(tecton) + 1);
                    } else {
                        // Ha nincs, akkor inicializáljuk
                        insectPositions.put(tecton, 0);
                    }
                    double[] offset = calculateOffset(insectPositions.get(tecton)); // vagy calculateOffset(index), ha több is lehet egy tectonon
                    double x = tVM.getX() + offset[0];
                    double y = tVM.getY() + offset[1];
                    addEntity(new InsectViewModel(insect, x, y));
                }
            }
        }

        // Ezután minden Tectonhoz tartozó Mushroom és Spore
        for (Tecton tecton : tectonVMs.keySet()) {
            TectonViewModel tVM = tectonVMs.get(tecton);

            // Gomba
            Mushroom mushroom = tecton.getMushroom();
            if (mushroom != null) {
                double[] offset = {0, 0};
                double x = tVM.getX() + offset[0];
                double y = tVM.getY() + offset[1];
                addEntity(new MushroomViewModel(mushroom, x, y));
            }

            // Spórák
            List<Spore> spores = tecton.getSpores();
            for (int i = 0; i < spores.size(); i++) {
                double[] offset = calculateSporeOffset(i);
                double x = tVM.getX() + offset[0];
                double y = tVM.getY() + offset[1];
                addEntity(new SporeViewModel(spores.get(i), x, y));
            }
        }

        // Ezután:
        // Thread-ek kirajzolása tectonok alapján
        Set<Thread> drawnThreads = new HashSet<>();
        for (Tecton tecton : tectonVMs.keySet()) {
            for (Thread thread : tecton.getThreads()) {
                if (drawnThreads.contains(thread)) continue;

                // Keresd meg a két tectont, amelyek tartalmazzák ezt a thread-et
                Tecton from = null, to = null;
                for (Tecton other : tectonVMs.keySet()) {
                    if (other.getThreads().contains(thread)) {
                        if (from == null) from = other;
                        else if (to == null && other != from) { to = other; break; }
                    }
                }

                Mushroom mushroom = thread.getParent();
                TectonViewModel fromVM = tectonVMs.get(from);
                TectonViewModel toVM = tectonVMs.get(to);

                // Keresd ki a MushroomViewModel-t az entities-ből:
                MushroomViewModel mushVM = null;
                for (EntityViewModel evm : entities) {
                    if (evm instanceof MushroomViewModel mvm && mvm.getModel() == mushroom) {
                        mushVM = mvm;
                        break;
                    }
                }

                if (fromVM != null && toVM != null && mushVM != null) {
                    addEntity(new ThreadViewModel(thread, fromVM, toVM, mushVM));
                    drawnThreads.add(thread);
                }
            }
        }

        System.out.println("Mycologists: " + mycologists);
        for (Mycologist m : mycologists) {
            System.out.println(m.getName() + " mushrooms: " + m.getMushrooms());
        }

        updatePlayerBox();
    }
    
    @FXML
    public void initialize() {
        instance = this;
        System.out.println("EntityController loaded.");
        // csak canvas layout binding vagy alapbeállítások
        Group mapGroup = new Group();
        canvas.getChildren().add(mapGroup);

        int rows = 4, cols = 4;
        double hexWidth = 70, hexHeight = 60;
        double totalMapWidth = hexWidth * cols + hexWidth / 2;
        double totalMapHeight = hexHeight * rows;

        mapGroup.layoutXProperty().bind(canvas.widthProperty().divide(2).subtract(totalMapWidth / 2));
        mapGroup.layoutYProperty().bind(canvas.heightProperty().divide(2).subtract(totalMapHeight / 2));

        entities.addListener((ListChangeListener<EntityViewModel>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (EntityViewModel vm : change.getAddedSubList()) {
                        Node view = createViewFor(vm);
                        if (view != null) {
                            canvas.getChildren().add(view);
                        }
                    }
                }
                if (change.wasRemoved()) {
                    canvas.getChildren().clear();
                    for (EntityViewModel vm : entities) {
                        Node view = createViewFor(vm);
                        if (view != null) {
                            canvas.getChildren().add(view);
                        }
                    }
                }
            }
        });
        
        sporeButton.setOnAction(e -> {
            if (selectedMushroom != null) {
                sporeShootMode = true;
                System.out.println("Válassz egy tectont, ahova spórát lősz!");
            }
        });

        growThreadButton.setOnAction(e -> {
            if (selectedMushroom != null) {
                growThreadMode = true;
                System.out.println("Válassz egy tectont, ahova fonalat növesztesz!");
            }
        });

        growMushroomButton.setOnAction(e -> {
            growMushroomMode = true;
            appendInfo("Válassz egy tectont, ahova gombát növesztesz!");
        });

        endTurnButton.setOnAction(e -> {
            int prevActorIndex = currentActorIndex;
            currentActorIndex = (currentActorIndex + 1) % turnOrder.size();
            currentActor = turnOrder.get(currentActorIndex);
            if (currentActor instanceof Mycologist) {
                currentMycologistName = ((Mycologist) currentActor).getName();
            }
            updateActionButtonsForTurn();
            updateTurnLabel();
            if (currentActorIndex == 0 && prevActorIndex == turnOrder.size() - 1) {
                Interpreter.executeCommand("/time");
            }
            refreshController(Interpreter.getObjects());
        });

        moveInsectButton.setOnAction(e -> {
            if (selectedInsect != null) {
                moveInsectMode = true;
                appendInfo("Válassz egy tectont, ahova mozgatod a rovart!");
            }
        });

        cutThreadButton.setOnAction(e -> {
            if (selectedInsect != null) {
                cutThreadMode = true;
                appendInfo("Válassz egy fonalat, amit elvágsz!");
            }
        });

        consumeSporeButton.setOnAction(e -> {
            if (selectedInsect != null) {
                String insectName = getObjectNameFor(selectedInsect.getModel());
                String cmd = "/consume -i " + insectName;
                fungorium.utils.Interpreter.executeCommand(cmd);
                endTurnButton.fire();
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

        // Sorrend: először minden gombász, aztán minden rovarász (vagy ahogy szeretnéd)
        turnOrder.clear();
        turnOrder.addAll(mycologists);
        turnOrder.addAll(insectists);

        currentActorIndex = 0;
        currentActor = turnOrder.get(0);

        updatePlayerBox();
        updateTurnLabel();
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
        tectonNodes.put(vm, hex);
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

        hex.setOnMouseExited((MouseEvent e) -> {
            hex.setFill(Color.BEIGE);
            Tecton tecton = vm.getModel();
            for (Tecton neighbor : tecton.getNeighbors()) {
                tectonNodes.get(tectonVMs.get(neighbor)).setFill(Color.BEIGE);
            }
        });

        hex.setOnMouseClicked((MouseEvent e) -> {
            if (sporeShootMode && selectedMushroom != null) {
                String mushroomName = getObjectNameFor(selectedMushroom.getModel());
                String tectonName = getObjectNameFor(vm.getModel());
                String cmd = "/shoot -m " + mushroomName + " -t " + tectonName;
                fungorium.utils.Interpreter.executeCommand(cmd);
                endTurnButton.fire();
                sporeShootMode = false;
            } else if (growThreadMode && selectedMushroom != null) {
                String mushroomName = getObjectNameFor(selectedMushroom.getModel());
                String tectonName = getObjectNameFor(vm.getModel());
                String cmd = "/growt -m " + mushroomName + " -tt " + tectonName;
                fungorium.utils.Interpreter.executeCommand(cmd);
                endTurnButton.fire();
                growThreadMode = false;
            } else if (growMushroomMode) {
                String tectonName = getObjectNameFor(vm.getModel());
                String cmd = "/growm -t " + tectonName + " -my " + currentMycologistName;
                fungorium.utils.Interpreter.executeCommand(cmd);
                endTurnButton.fire();
                growMushroomMode = false;
            } else if (moveInsectMode && selectedInsect != null) {
                String insectName = getObjectNameFor(selectedInsect.getModel());
                String tectonName = getObjectNameFor(vm.getModel());
                String cmd = "/move -i " + insectName + " -t " + tectonName;
                fungorium.utils.Interpreter.executeCommand(cmd);
                endTurnButton.fire();
                moveInsectMode = false;
            } else {
                System.out.println("This tecton has been clicked at position: (" + vm.getX() + ", " + vm.getY() + ")");
                hex.setFill(Color.RED);
                Tecton tecton = vm.getModel();
                for (Tecton neighbor : tecton.getNeighbors()) {
                    tectonNodes.get(tectonVMs.get(neighbor)).setFill(Color.RED);
                }
            }
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
        if (currentActor instanceof Mycologist m && m == player) {
            triangle.setFill(Color.DARKRED);
        } else {
            triangle.setFill(Color.DARKOLIVEGREEN);
        }
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
            if (currentActor instanceof Mycologist m && m == player) {
                selectedMushroom = vm;
                sporeShootMode = false;
                updateActionButtonsForMushroom();
            }
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
        if (currentActor instanceof Insectist i && i == player) {
            circle.setFill(Color.DARKRED);
        } else {
            circle.setFill(Color.LIGHTBLUE);
        }
        circle.setStroke(Color.DARKGRAY);

        Group group = new Group(circle);
        group.layoutXProperty().bind(vm.xProperty());
        group.layoutYProperty().bind(vm.yProperty());

        group.setOnMouseEntered((MouseEvent e) -> {
            Tooltip tooltip = new Tooltip("This insect belongs to " + player.getName());
            Tooltip.install(group, tooltip);
        });

        group.setOnMouseClicked((MouseEvent e) -> {
            if (currentActor instanceof Insectist i && i == player) {
                selectedInsect = vm;
                updateActionButtonsForInsect();
            }
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
        javafx.scene.shape.Line line = new javafx.scene.shape.Line();
        line.startXProperty().bind(vm.getFrom().xProperty());
        line.startYProperty().bind(vm.getFrom().yProperty());
        line.endXProperty().bind(vm.getTo().xProperty());
        line.endYProperty().bind(vm.getTo().yProperty());
        line.setStrokeWidth(5.0);
        if (currentActor instanceof Mycologist i && i.getMushrooms().contains(vm.getMushroom().getModel())) {
            line.setStroke(Color.DARKRED);
        } else {
            line.setStroke(Color.DARKMAGENTA);
        }

        line.setOnMouseClicked(e -> {
            if (cutThreadMode && selectedInsect != null) {
                String insectName = getObjectNameFor(selectedInsect.getModel());
                String threadName = getObjectNameFor(vm.getModel());
                String cmd = "/cut -i " + insectName + " -th " + threadName;
                fungorium.utils.Interpreter.executeCommand(cmd);
                endTurnButton.fire();
                cutThreadMode = false;
            }
        });

        return line;
    }

    public void refreshViewModels() {
        // későbbi frissítéshez
    }

    @FXML
    private void onTestButtonClicked() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fungorium/gui/TestSelector.fxml"));
        Parent root = loader.load();

        // Átadjuk az aktuális EntityController példányt a TestSelectorController-nek
        TestSelectorController selectorController = loader.getController();
        selectorController.setEntityController(this);

        Stage stage = new Stage();
        stage.setTitle("Teszt kiválasztása");
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Ezt a metódust hívja majd a TestSelectorController, ha kiválasztottak egy tesztet
    public void runSelectedTest(String testFolder) {
        try {
            // Bezárjuk a tesztválasztó ablakot
            fungorium.gui.FungoriumApp.runTestFile("tests/" + testFolder + "/input.txt", this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateActionButtonsForMushroom() {
        sporeButton.setVisible(true);
        growThreadButton.setVisible(true);
        growMushroomButton.setVisible(true);
        // A többi gomb rejtése
        // (ha több gombot is akarsz, azokat is állítsd false-ra)
        // pl. moveInsectButton.setVisible(false); stb.
        cutThreadButton.setVisible(false);
        consumeSporeButton.setVisible(false);
        moveInsectButton.setVisible(false);
    }

    private void updateActionButtonsForInsect() {
        moveInsectButton.setVisible(true);
        cutThreadButton.setVisible(true);
        consumeSporeButton.setVisible(true);
        // A többi gomb rejtése
        sporeButton.setVisible(false);
        growThreadButton.setVisible(false);
        growMushroomButton.setVisible(false);
    }


    public void appendInfo(String message) {
        Label label = new Label(message);
        label.setWrapText(true);
        //label.maxWidthProperty().bind(infoScrollPane.widthProperty().subtract(20));
        label.setMaxWidth(160);
        label.setMinHeight(40);
        infoPanel.getChildren().add(label);
        if (infoPanel.getChildren().size() > 30) {
            infoPanel.getChildren().remove(0);
        }
        // Automatikus görgetés a legalsóhoz:
        infoScrollPane.layout();
        infoScrollPane.setVvalue(1.0);
    }

    private static String getObjectNameFor(Object obj) {
        for (Map.Entry<String, Object> entry : fungorium.utils.Interpreter.getObjectNames().entrySet()) {
            if (entry.getValue() == obj) {
                return entry.getKey();
            }
        }
        return null;
    }

    private String currentMycologistName = "mycologist1"; // vagy dinamikusan állítod majd körváltásnál
    private List<Object> turnOrder = new ArrayList<>(); // Mycologist és Insectist példányok vegyesen
    private int currentActorIndex = 0;
    private Object currentActor = null; // mindig az aktuális játékos (Mycologist vagy Insectist)

    private void updateTurnLabel() {
        // Ha ez az utolso jatekos, es a kor vegere ertunk
        if (currentActor instanceof Mycologist m) {
            turnLabel.setText("Gombász lép: " + m.getName());
        } else if (currentActor instanceof Insectist i) {
            turnLabel.setText("Rovarász lép: " + i.getName());
        }
    }

    private void updateActionButtonsForTurn() {
        if (currentActor instanceof Mycologist) {
            // Csak a gombász gombjai látszanak
            growMushroomButton.setVisible(true);
            sporeButton.setVisible(false);
            growThreadButton.setVisible(false);
            moveInsectButton.setVisible(false);
            cutThreadButton.setVisible(false);
            consumeSporeButton.setVisible(false);
        } else if (currentActor instanceof Insectist) {
            // Csak a rovarász gombjai látszanak (alapból rejtve, csak rovarra kattintva jelennek meg)
            growMushroomButton.setVisible(false);
            sporeButton.setVisible(false);
            growThreadButton.setVisible(false);
            // Ezek csak rovarra kattintva lesznek láthatók:
            moveInsectButton.setVisible(false);
            cutThreadButton.setVisible(false);
            consumeSporeButton.setVisible(false);
        }
    }
}