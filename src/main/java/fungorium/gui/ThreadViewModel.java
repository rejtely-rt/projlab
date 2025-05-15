package fungorium.gui;

import fungorium.model.Thread;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ThreadViewModel implements EntityViewModel {
    private final Thread model;
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();

    public ThreadViewModel(Thread model, double startX, double startY) {
        this.model = model;
        this.x.set(startX);
        this.y.set(startY);
        refreshFromModel();
    }

    public double getX() { return x.get(); }
    public double getY() { return y.get(); }
    public DoubleProperty xProperty() { return x; }
    public DoubleProperty yProperty() { return y; }

    public Thread getModel() {
        return model;
    }

    @Override
    public void refreshFromModel() {
        // Implement any necessary updates from the model here
    }
}
