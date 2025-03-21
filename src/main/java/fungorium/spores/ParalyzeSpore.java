package fungorium.spores;

import fungorium.model.Insect;

public class ParalyzeSpore extends Spore {

    @Override
    public void applyEffect(Insect target) {
        target.changeSpeed(0);
    }
}