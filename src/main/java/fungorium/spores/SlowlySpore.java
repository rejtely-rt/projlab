package fungorium.spores;

import fungorium.model.Insect;

public class SlowlySpore extends Spore {
    /**
     * Applies the effect of the SlowlySpore to the target insect.
     * This effect slows down the insect by setting its speed to 1.
     *
     * @param target the insect that will be affected by the spore
     */
    @Override
    public void applyEffect(Insect target) {
        target.changeSpeed(1);
    }
}