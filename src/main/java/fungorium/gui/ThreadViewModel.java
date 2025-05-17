package fungorium.gui;

import fungorium.model.Thread;

public class ThreadViewModel implements EntityViewModel {
    private final Thread model;
    private final TectonViewModel from;
    private final TectonViewModel to;
    private final MushroomViewModel mushroom;

    public ThreadViewModel(Thread model, TectonViewModel from, TectonViewModel to, MushroomViewModel mushroom) {
        this.model = model;
        this.from = from;
        this.to = to;
        this.mushroom = mushroom;
    }

    public TectonViewModel getFrom() { return from; }
    public TectonViewModel getTo() { return to; }
    public MushroomViewModel getMushroom() { return mushroom; }
    public Thread getModel() { return model; }

    @Override
    public double getX() { return mushroom.getX(); }
    @Override
    public double getY() { return mushroom.getY(); }
    @Override
    public void refreshFromModel() {}
}
