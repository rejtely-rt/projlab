package fungorium.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import fungorium.model.*;

public class InsectViewModel implements EntityViewModel {
    private final Insect model;
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();

    public InsectViewModel(Insect model, double x, double y) {
        this.model = model;
        this.x.set(x);
        this.y.set(y);
    }

    @Override
    public double getX() {
        return x.get();
    }

    @Override
    public double getY() {
        return y.get();
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public Insect getModel() {
        return model;
    }

    @Override
    public void refreshFromModel() {
        // később: modellváltozások lekövetése
    }
}
