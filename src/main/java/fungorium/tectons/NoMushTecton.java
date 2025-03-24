package fungorium.tectons;

import fungorium.model.Mushroom;
import fungorium.utils.Logger;

public class NoMushTecton extends Tecton {
    public NoMushTecton() {
    }
    
    public NoMushTecton(Mushroom m) {
        this.mushroom = m;
    }

    @Override
    public boolean addMushroom() {
        Logger.enter(this, "addMushroom");
        System.out.println("   -> NoMushTecton cannot accept mushrooms. Operation denied.");
        Logger.exit(false);
        return false;
    }
}