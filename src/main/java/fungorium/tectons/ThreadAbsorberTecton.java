package fungorium.tectons;

import fungorium.model.Mushroom;
import fungorium.utils.Logger;

public class ThreadAbsorberTecton extends Tecton {
    public ThreadAbsorberTecton() {
    }
    
    public ThreadAbsorberTecton(Mushroom m) {
        this.mushroom = m;
    }

   @Override 
    public void applyEffect() {
        Logger.enter(this, "applyEffect");
        this.absorbThread();
        Logger.exit("");
    }
}
