package fungorium.spores;

import fungorium.model.Insect;
import fungorium.utils.Logger;

public class CannotCutSpore extends Spore {

    @Override
    public void applyEffect(Insect insect) {
        Logger.enter(this, "applyEffect");
        insect.changeCut(false);
        Logger.exit("");
    }
}