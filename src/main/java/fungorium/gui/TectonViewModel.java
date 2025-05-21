package fungorium.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import java.util.ArrayList;
import java.util.List;

import fungorium.tectons.Tecton;

public class TectonViewModel implements EntityViewModel {
    private final Tecton model;
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();

    private final List<EntityViewModel> entities = new ArrayList<>();

    public List<EntityViewModel> getEntities() {
        return entities;
    }

    public void addEntity(EntityViewModel entity) {
        entities.add(entity);
    }

    public void removeEntity(EntityViewModel entity) {
        entities.remove(entity);
    }

    public TectonViewModel(Tecton model, double x, double y) {
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

    @Override
    public void refreshFromModel() {
        // később: modellváltozások lekövetése
    }

    public Tecton getModel() {
        return model;
    }
}