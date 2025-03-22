package fungorium.spores;

import fungorium.model.Insect;

public class CannotCutSpore extends Spore {

    @Override
    public void applyEffect(Insect insect) {
        insect.changeCut(false);
    }
}