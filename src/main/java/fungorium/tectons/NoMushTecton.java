package fungorium.tectons;

import fungorium.utils.Logger;

public class NoMushTecton extends Tecton {
    public NoMushTecton() {
        Logger.create(this);
    }

    @Override
    public boolean addMushroom() {
        Logger.enter(this, "addMushroom");
        System.out.println("   -> NoMushTecton cannot accept mushrooms. Operation denied.");
        Logger.exit(false);
        return false;
    }
}