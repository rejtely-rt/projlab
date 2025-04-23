package fungorium.spores;

import fungorium.model.Insect;
import fungorium.utils.Logger;

public class SpeedySpore extends Spore {

    /**
     * Applies the effect of the SpeedySpore to the target insect.
     * This effect increases the speed of the target insect by setting its speed to 3.
     *
     * @param target the insect to which the effect will be applied
     */
    @Override
    public void applyEffect(Insect target) {
        Logger.enter(this, "applyEffect");
        target.changeSpeed(3);
        Logger.exit("");
    }
}