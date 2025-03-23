package fungorium.spores;

import fungorium.model.Insect;
import fungorium.utils.Logger;

public class ParalyzeSpore extends Spore {

    @Override
    public void applyEffect(Insect target) {
        Logger.enter(this, "applyEffect");
        target.changeSpeed(0);
        Logger.exit("");
    }
}