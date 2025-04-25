package fungorium.spores;

import fungorium.model.Insect;
import fungorium.utils.Logger;

public abstract class Spore {

    private int cooldown; // Cooldown attribútum hozzáadása

    public Spore() {
        Logger.create(this);
        this.cooldown = 0; // Alapértelmezett érték
    }

    /**
     * Gets the cooldown value of the spore.
     *
     * @return the cooldown value.
     */
    public int getCooldown() {
        Logger.enter(this, "getCooldown");
        Logger.exit(cooldown);
        return cooldown;
    }

    /**
     * Decreases the cooldown period for the spore.
     */
    public void decreaseCooldown() {
        Logger.enter(this, "decreaseCooldown");
        if (cooldown > 0) {
            cooldown--; // Csökkenti a cooldown értékét
        }
        Logger.exit(cooldown);
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
        Logger.enter(this, "removeEffect");
        target.changeSpeed(2);
        target.changeCut(true);
        Logger.exit("");
    }
}