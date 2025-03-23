package fungorium.model;

import fungorium.spores.Spore;
import fungorium.tectons.Tecton;
import fungorium.model.Thread;
import java.util.List;
import java.util.ArrayList;
import fungorium.utils.Logger;

public class Insect {

    /**
     * It is the current location of the insect.
     */
    private Tecton location;

    /**
     * Spores is defined by a list of Spore objects.
     * It is the spores that the insect has consumed.
     */
    private final List<Spore> spores;

    /**
     * Constructor of the Insect class.
     * It initializes the speed, cut and spores fields.
     */
    public Insect() {
        Logger.enter(this, "Insect");
        this.spores = new ArrayList<>();
        Logger.create(this);
        Logger.exit("");
    }

    /**
     * Getter method for the speed field.
     * @return the speed of the insect
     */
    public int getSpeed() {
        Logger.enter(this, "getSpeed");
        int speed = Logger.question("What is the insect's speed?") ? 2 : 0;
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
    List<Spore> getScore() { return spores; }

    /**
     * Change the speed of the insect.
     * @param value the new speed value
     */
    public void changeSpeed(int value) {
        Logger.enter(this, "changeSpeed");
        Logger.exit(value);
    }

    /**
     * Change the cut of the insect.
     * @param value the new cut value
     */
    public void changeCut(boolean value) {
        Logger.enter(this, "changeCut");
        Logger.exit(value);
    }

    /**
     * Change the location of the insect.
     * @param target the new location
     * @return true if the insect can move to the target location, false otherwise
     * @note The insect can move to the target location if the speed is not 0 and there is
     *       a connecting thread between the current and target locations.
     */
    public void moveTo(Tecton target) {
        Logger.enter(this, "moveTo");
        if (getSpeed() == 0) {
            Logger.exit(""); // If the speed is 0, the insect cannot move
            return;
        }

        if (this.location == null || target == null) {
            Logger.exit(""); // If the current or target Tecton is null, the insect cannot move
            return;
        }

        // Current and target Tecton threads
        List<Thread> currentThreads = this.location.getThreads();
        List<Thread> targetThreads = target.getThreads();
        for (Thread thread : currentThreads) {
            if (targetThreads.contains(thread)) { // If there is a connecting thread
                this.location = target; // Successful move
                Logger.exit("");
                return;
            }
        }
        Logger.exit(""); // If there is no connecting thread, the insect cannot move
    }
    /**
     * Cut the given thread.
     * @param thread the thread to be cut
     * @return true if the thread is cut, false otherwise
     * @note The insect can cut the thread and remove it from the parent mushroom.
     */
    public boolean cutThread(Thread thread) {
        Logger.enter(this, "cutThread");
        if (!getCut()) {
            Logger.exit(false);
            return false;
        }
        Mushroom parentMushroom = thread.getParent();
        if (parentMushroom != null) {
            parentMushroom.removeThread(thread);
        }
        if (this.location != null) {
            List<Tecton> neighbors = this.location.getNeighbors();
            for (Tecton t : neighbors) {
                if (t.getThreads().isEmpty()) {
                    assert parentMushroom != null;
                    parentMushroom.threadCollector(t);
                }
            }
        }
        Logger.exit(true);
        return true;
    }

    /**
     * Consume the given spore.
     * @note The insect can consume the spore and apply its effect.
     */
    public void consumeSpore(){
        Logger.enter(this, "consumeSpore");
        Spore spore = this.location.getSpores().get(0);
        spore.applyEffect(this);
        this.spores.add(spore);
        Logger.exit(null);
    }

    /**
     * Check if the insect can consume the given spore.
     * @return true if the insect is under the effect if any spore, false otherwise
     * @note If the speed is normal and the cut is true, the is not under the effect of any spore.
     */
    public boolean coolDownCheck() {
        Logger.enter(this, "coolDownCheck");
        boolean result = getSpeed() == 2 && getCut();
        Logger.exit(result);
        return result;
    }
}