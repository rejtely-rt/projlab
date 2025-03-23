package fungorium.spores;

import fungorium.model.Insect;
import fungorium.utils.Logger;

public abstract class Spore {

    public Spore () {
        Logger.create(this);
    }

    public int getCooldown() {
        Logger.enter(this, "getCooldown");
        int value = Logger.questionNumber("What is the cooldown of the spore?");
        Logger.exit(value);
        return value;
    }


    public void decreaseCooldown() {
        Logger.enter(this, "decreaseCooldown");

        int currentCooldown = getCooldown();
        if (currentCooldown == -1) {
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