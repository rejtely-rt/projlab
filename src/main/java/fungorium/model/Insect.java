package fungorium.model;

import fungorium.spores.Spore;
import fungorium.tectons.Tecton;
import java.util.List;
import java.util.ArrayList;

import fungorium.utils.Interpreter;
import fungorium.utils.Logger;

public class Insect {
    /**
     * It is the current location of the insect.
     */
    private Tecton location;

    /**
     * It is the life of the insect.
     * If the insect is dead, it cannot move or consume spores.
     * If the insect is alive, it can move and consume spores.
     */
    private boolean life;

    /**
     * Spores is defined by a list of Spore objects.
     * It is the spores that the insect has consumed.
     */
    private final List<Spore> spores;

    /**
     * Determines if the insect can cut threads.
     */
    private boolean cut;

    /**
     * Represents the speed of the insect.
     */
    private int speed;

    /**
     * Constructor of the Insect class.
     * It initializes the speed, cut, and spores fields with default values.
     */
    public Insect() {
        this.spores = new ArrayList<>();
        this.cut = true;
        this.speed = 2;
        Interpreter.create(this);
    }

    public void setLocation(Tecton location) {
        Logger.enter(this, "setLocation");
        this.location = location;
        Logger.exit("");
    }

    public void setSpeed(int speed) {
        Logger.enter(this, "setSpeed");
        this.speed = speed;
        Logger.exit("");
    }

    public void setCut(boolean cut) {
        Logger.enter(this, "setCut");
        this.cut = cut;
        Logger.exit("");
    }

    /**
     * Getter method for the speed field.
     * @return the speed of the insect
     */
    public int getSpeed() {
        Logger.enter(this, "getSpeed");
        int speed = Logger.questionNumber("What is the speed of the insect?");
        Logger.exit(speed);
        return speed;
    }

    /**
     * Getter method for the cut field.
     * @return the cut of the insect
     */
    public boolean getCut() {
        Logger.enter(this, "getCut");
        boolean cut = Logger.question("Can the insect cut?");
        Logger.exit(cut);
        return cut;
    }

    /**
     * Getter method for the location field.
     * @return the location of the insect
     */
    public Tecton getLocation() {
        Logger.enter(this, "getLocation");
        Logger.exit(location);
        return location;
    }

    /**
     * Getter method for the spores field.
     * @return the spores of the insect
     */
    public List<Spore> getSpores() {
        return spores;
    }

    /**
     * Getter method for the life field.
     * @return the life of the insect
     */
    public boolean getLife() {
        Logger.enter(this, "getLife");
        Logger.exit(life);
        return life;
    }

    /**
     * Setter method for the life field.
     * @param life the new life value
     */
    public void setLife(boolean life) {
        Logger.enter(this, "setLife");
        this.life = life;
        Logger.exit("");
    }

    /**
     * Change the speed of the insect.
     * @param value the new speed value
     */
    public void changeSpeed(int value) {
        Logger.enter(this, "changeSpeed");
        this.speed += value;
        Logger.exit("");
    }

    /**
     * Change the cut of the insect.
     * @param value the new cut value
     */
    public void changeCut(boolean value) {
        Logger.enter(this, "changeCut");
        this.cut = value;
        Logger.exit("");
    }

    /**
     * Change the location of the insect.
     * @param target the new location
     * @return true if the insect can move to the target location, false otherwise
     */
    public void moveTo(Tecton target) {
        Logger.enter(this, "moveTo");
        if (this.location == null) {
            this.location = target;
            Logger.exit(false);
            return;
        }
        if (getSpeed() <= 0) {
            Logger.exit(false);
            return;
        }

        List<Thread> currentThreads = this.location.getThreads();
        List<Thread> targetThreads = target.getThreads();
        for (Thread thread : currentThreads) {
            if (targetThreads.contains(thread)) {
                this.location = target;
                Logger.exit(true);
                return;
            }
        }
        Logger.exit(false);
    }

    /**
     * Cut the given thread.
     * @param thread the thread to be cut
     * @return true if the thread is cut, false otherwise
     */
    public boolean cutThread(Thread thread) {
        Logger.enter(this, "cutThread");
        if (!getCut()) {
            Logger.exit(false);
            return false;
        }
        thread.setCutOff(true);
        Logger.exit(true);
        return true;
    }

    /**
     * Consume the given spore.
     */
    public void consumeSpore() {
        Logger.enter(this, "consumeSpore");
        List<Spore> sporeList = this.location.getSpores();
        if (sporeList.isEmpty()) {
            Logger.exit(null);
            return;
        }
        Spore spore = sporeList.get(0);
        sporeList.remove(0);
        spore.applyEffect(this);
        this.spores.add(spore);
        Logger.exit(null);
    }

    /**
     * Check if the insect can consume the given spore.
     */
    public void coolDownCheck() {
        Logger.enter(this, "coolDownCheck");
        for (Spore spore : spores) {
            spore.decreaseCooldown();
            int cooldown = spore.getCooldown();
            if (cooldown == 0) {
                spore.removeEffect(this);
            }
        }
        Logger.exit("");
    }
}