package fungorium.tectons;

import fungorium.utils.Logger;

public class ThreadAbsorberTecton extends Tecton {
    public ThreadAbsorberTecton() {
        Logger.create(this);
    }

    public void applyEffect() {
        Logger.enter(this, "applyEffect");
        this.absorbThread();
        Logger.exit("");
    }
}
