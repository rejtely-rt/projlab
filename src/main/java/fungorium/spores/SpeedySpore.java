package fungorium.spores;

import fungorium.model.Insect;

public class SpeedySpore extends Spore {

    @Override
    public void applyEffect(Insect target) {
        target.changeSpeed(3);
    }
}