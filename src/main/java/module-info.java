module fungorium {
    requires javafx.controls;
    requires javafx.fxml;

    opens fungorium to javafx.fxml;
    opens fungorium.gui to javafx.fxml;

    exports fungorium;
    exports fungorium.gui;
}