package fungorium.spores;

import fungorium.model.Insect;
import fungorium.utils.Logger;

public abstract class Spore {

    public Spore () {
        Logger.create(this);
    }

    public int getCooldown() {
        Logger.enter(this, "getCooldown");

        if (Logger.question("Is the cooldown 0?")) {
            Logger.exit(0);
            return 0;
        }
        if (Logger.question("Is the cooldown 1?")) {
            Logger.exit(1);
            return 1;
        }
        if (Logger.question("Is the cooldown 2?")) {
            Logger.exit(2);
            return 2;
        }
        if (Logger.question("Is the cooldown 3?")) {
            Logger.exit(3);
            return 3;
        }

        // Ha valamiért egyik válasz sem volt megfelelő, alapértelmezésként 0-t adunk vissza.
        Logger.exit(0);
        return 0;
    }


    public void decreaseCooldown() {
        Logger.enter(this, "decreaseCooldown");

        int currentCooldown = getCooldown();
        if (currentCooldown == 0) {
            Logger.exit(""); // A cooldown már nem csökkenhet tovább
            return;
        }

        if (Logger.question("Decrease cooldown to " + (currentCooldown - 1) + "?")) {
            Logger.exit("");
        } else {
            Logger.exit(""); // Ha nem csökkentjük, marad az eredeti érték
        }
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
        Logger.enter(this, "removeEffect");
        target.changeSpeed(2);
        target.changeCut(true);

        Logger.exit("");
    }
}