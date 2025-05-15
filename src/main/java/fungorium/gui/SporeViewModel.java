package fungorium.gui;

import fungorium.spores.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class SporeViewModel implements EntityViewModel {

    private final Spore model;

    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();

    public SporeViewModel(Spore model, double startX, double startY) {
        this.model = model;
        this.x.set(startX);
        this.y.set(startY);
        refreshFromModel();
    }
    public Spore getModel() {
        return model;
    }
    public double getX() { return x.get(); }
    public double getY() { return y.get(); }
    public DoubleProperty xProperty() { return x; }
    public DoubleProperty yProperty() { return y; }

    @Override
    public void refreshFromModel() {
        // Implement any necessary updates from the model here
    }
}
