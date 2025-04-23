package fungorium.spores;

import fungorium.model.Insect;
import fungorium.utils.Logger;

public class CannotCutSpore extends Spore {

    /**
     * Applies the effect of this spore to the given insect.
     * This effect prevents the insect from being able to cut.
     *
     * @param insect the insect to which the effect is applied
     */
    @Override
    public void applyEffect(Insect insect) {
        Logger.enter(this, "applyEffect");
        insect.changeCut(false);
        Logger.exit("");
    }
}