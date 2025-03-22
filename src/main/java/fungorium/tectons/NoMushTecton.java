package fungorium.tectons;

import fungorium.model.Mushroom;
import fungorium.utils.Logger;

public class NoMushTecton extends Tecton {
    public NoMushTecton() {
        Logger.create(this);
    }

    public void addMushroom(Mushroom m) {
        Logger.enter(this, "addMushroom");
        System.out.println("   -> NoMushTecton cannot accept mushrooms. Operation denied.");
        Logger.exit("");
    }
}