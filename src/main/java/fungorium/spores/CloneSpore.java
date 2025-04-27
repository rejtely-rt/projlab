package fungorium.spores;

import fungorium.model.Insect;

public class CloneSpore extends Spore {

    /**
     * Applies the effect of this spore to the given insect.
     * This effect causes the insect to clone itself, creating a new insect with the same attributes.
     *
     * @param insect the insect to which the effect is applied
     */
    @Override
    public void applyEffect(Insect insect) {

        // Cloning the insect (creating a new insect instance)
        /*Insect clonedInsect = new Insect();
        clonedInsect.setLocation(insect.getLocation());
        clonedInsect.setSpeed(insect.getSpeed());
        clonedInsect.setCut(insect.getCut());*/
        insect.clone();

    }
}
