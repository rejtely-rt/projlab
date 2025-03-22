package fungorium.spores;

import fungorium.model.Insect;

public class SlowlySpore extends Spore {
    @Override
    public void applyEffect(Insect target) {
        target.changeSpeed(1);
    }
}