package fungorium.spores;

import fungorium.model.Insect;
import fungorium.utils.Logger;

public class ParalyzeSpore extends Spore {

    /**
     * Applies the paralyzing effect to the target insect by setting its speed to zero.
     *
     * @param target the insect that will be affected by the paralyzing spore
     */
    @Override
    public void applyEffect(Insect target) {
        Logger.enter(this, "applyEffect");
        target.changeSpeed(0);
        Logger.exit("");
    }
}