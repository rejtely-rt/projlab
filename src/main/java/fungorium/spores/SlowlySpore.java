package fungorium.spores;

import fungorium.model.Insect;
import fungorium.utils.Logger;

public class SlowlySpore extends Spore {
    /**
     * Applies the effect of the SlowlySpore to the target insect.
     * This effect slows down the insect by setting its speed to 1.
     *
     * @param target the insect that will be affected by the spore
     */
    @Override
    public void applyEffect(Insect target) {
        Logger.enter(this, "applyEffect");
        target.changeSpeed(1);
        Logger.exit("");
    }
}