package fungorium.spores;

import fungorium.model.Insect;
import fungorium.utils.Logger;

public class SpeedySpore extends Spore {

    @Override
    public void applyEffect(Insect target) {
        Logger.enter(this, "applyEffect");
        target.changeSpeed(3);
        Logger.exit("");
    }
}