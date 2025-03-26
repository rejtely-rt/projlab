package fungorium.tectons;

import fungorium.model.Mushroom;
import fungorium.utils.Logger;

public class NoMushTecton extends Tecton {
    public NoMushTecton() {
        Logger.create(this);
    }
    
    public NoMushTecton(Mushroom m) {
        System.out.println("   -> NoMushTecton cannot accept mushrooms. Operation denied.");
    }

    /**
     * Attempts to add a mushroom to the NoMushTecton.
     * 
     * @return false always, as NoMushTecton cannot accept mushrooms.
     */
    @Override
    public boolean addMushroom() {
        Logger.enter(this, "addMushroom");
        System.out.println("   -> NoMushTecton cannot accept mushrooms. Operation denied.");
        Logger.exit(false);
        return false;
    }
}