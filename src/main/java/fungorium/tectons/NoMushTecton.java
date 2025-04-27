package fungorium.tectons;

import fungorium.model.Mushroom;
public class NoMushTecton extends Tecton {
    public NoMushTecton() {
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
        System.out.println("   -> NoMushTecton cannot accept mushrooms. Operation denied.");
        return false;
    }
}