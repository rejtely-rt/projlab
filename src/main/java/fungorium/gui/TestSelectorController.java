package fungorium.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.File;

public class TestSelectorController {
    @FXML
    private ListView<String> testList;
    @FXML
    private Button runButton;

    private EntityController entityController;

    public void setEntityController(EntityController controller) {
        this.entityController = controller;
    }

    @FXML
    public void initialize() {
        File testsDir = new File("tests");
        if (testsDir.exists() && testsDir.isDirectory()) {
            for (File test : testsDir.listFiles()) {
                if (test.isDirectory()) {
                    testList.getItems().add(test.getName());
                }
            }
        }
        // Sort test names
        testList.getItems().sort((a, b) -> {
            int numA = extractNumber(a);
            int numB = extractNumber(b);
            return Integer.compare(numA, numB);
        });        
        
        runButton.setOnAction(e -> {
            String selected = testList.getSelectionModel().getSelectedItem();
            if (selected != null && entityController != null) {
                entityController.runSelectedTest(selected);
                ((Stage)runButton.getScene().getWindow()).close();
            }
        });
    }

    // Helper method for extracting numbers from the string
    private static int extractNumber(String name) {
        try {
            return Integer.parseInt(name.replaceAll("\\D+", ""));
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }
}