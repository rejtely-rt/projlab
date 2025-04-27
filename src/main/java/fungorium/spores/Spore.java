package fungorium.spores;

import fungorium.model.Insect;
import fungorium.utils.Interpreter;

public abstract class Spore {

    private int cooldown; // Cooldown attribútum hozzáadása

    public Spore() {
        Interpreter.create(this);
        this.cooldown = 0; // Alapértelmezett érték
    }

    /**
     * Gets the cooldown value of the spore.
     *
     * @return the cooldown value.
     */
    public int getCooldown() {
        return cooldown;
    }

    /**
     * Decreases the cooldown period for the spore.
     */
    public void decreaseCooldown() {
        if (cooldown > 0) {
            cooldown--; // Csökkenti a cooldown értékét
        }
    }

    /**
     * Apply the effect of the spore to the target insect.
     * @param target the insect that the spore is applied to
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