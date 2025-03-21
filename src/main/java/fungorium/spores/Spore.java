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

    public abstract void applyEffect(Insect target);

    public void removeEffect(Insect target) {
        target.changeSpeed(2);
        target.changeCut(true);
    }
}