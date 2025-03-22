package fungorium.spores;

import fungorium.model.Insect;

public abstract class Spore {
    private int cooldown;

    public int getCooldown() {
        return cooldown; // Return the current cooldown value
    }

    public void decreaseCooldown() {
        cooldown--; // Decrease the cooldown value by 1
    }

    /**
     * Apply the effect of the spore to the target insect.
     * @param target the insect that the spore is applied to
     * @note It is an abstract method that should be implemented by the subclasses.
     */
    public abstract void applyEffect(Insect target);

    /**
     * Remove the effect of the spore from the target insect.
     * @param target the insect that the spore is applied to
     */
    public void removeEffect(Insect target) {
        target.changeSpeed(2);
        target.changeCut(true);
    }
}