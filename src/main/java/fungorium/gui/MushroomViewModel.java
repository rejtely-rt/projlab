package fungorium.gui;

import javafx.beans.property.*;
import fungorium.model.Mushroom;

public class MushroomViewModel implements EntityViewModel {
    private final Mushroom model;
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();
    private final IntegerProperty level = new SimpleIntegerProperty();

    public MushroomViewModel(Mushroom model) {
        this.model = model;
        refreshFromModel();
    }

    @Override
    public void refreshFromModel() {
        x.set(model.getX());
        y.set(model.getY());
        level.set(model.getLevel());
    }

    public double getX() { return x.get(); }
    public double getY() { return y.get(); }

    public DoubleProperty xProperty() { return x; }
    public DoubleProperty yProperty() { return y; }
    public IntegerProperty levelProperty() { return level; }

    public Mushroom getModel() { return model; }
}