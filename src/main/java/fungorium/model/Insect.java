package fungorium.model;

import fungorium.spores.CannotCutSpore;
import fungorium.spores.Spore;
import fungorium.tectons.Tecton;
import fungorium.model.Thread;
import java.util.List;
import java.util.ArrayList;

public class Insect {

    /**
     * Speed if the insect is defined by an integer value.
     * In norman conditions, speed is 2. It can be fastened,
     * lowered or set to 0 by spores.
     */
    private int speed;

    /**
     * In norman conditions, cut is true. It can be set to false by spores.
     */
    private boolean cut;

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
        this.speed = 2;
        this.cut = true;
        this.spores = new ArrayList<>();
    }

    /**
     * Getter method for the speed field.
     * @return the speed of the insect
     */
    public int getSpeed() {
        return speed;
    }


    /**
     * Getter method for the cut field.
     * @return the cut of the insect
     */
    public boolean getCut() {
        return cut;
    }

    /**
     * Getter method for the location field.
     * @return the location of the insect
     */
    public Tecton getLocation() {
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
        this.speed = value;
    }

    /**
     * Change the cut of the insect.
     * @param value the new cut value
     */
    public void changeCut(boolean value) {
        this.cut = value;
    }

    /**
     * Change the location of the insect.
     * @param target the new location
     * @return true if the insect can move to the target location, false otherwise
     * @note The insect can move to the target location if the speed is not 0 and there is
     *       a connecting thread between the current and target locations.
     */
    public boolean moveTo(Tecton target) {
        if (this.speed == 0) {
            return false; // If the speed is 0, the insect cannot move
        }

        if (this.location == null || target == null) {
            return false; // If the current or target Tecton is null, the insect cannot move
        }

        // Current and target Tecton threads
        List<Thread> currentThreads = this.location.getThreads();
        List<Thread> targetThreads = target.getThreads();

        for (Thread thread : currentThreads) {
            if (targetThreads.contains(thread)) {   // If there is a connecting thread
                this.location = target; // Successful move
                return true;
            }
        }

        return false; // If there is no connecting thread, the insect cannot move
    }

    /**
     * Cut the given thread.
     * @param thread the thread to be cut
     * @return true if the thread is cut, false otherwise
     * @note The insect can cut the thread and remove it from the parent mushroom.
     */
    public boolean cutThread(Thread thread) {
        if (!this.cut) {
            return false; // If the insect cannot cut, the thread cannot be cut
        }


        // The parent mushroom of the thread
        Mushroom parentMushroom = thread.getParent();
        if (parentMushroom != null) {
            parentMushroom.removeThread(thread);
        }

        // If the thread is cut, the insect cannot move
        if (this.location != null) {
            List<Tecton> neighbors = this.location.getNeighbors();
            for (Tecton t : neighbors) {
                if (t.getThreads().isEmpty()) { // If the Tecton has no threads
                    assert parentMushroom != null;
                    parentMushroom.threadCollector(t);
                }
            }
        }
        return true;
    }

    /**
     * Consume the given spore.
     * @param spore the spore to be consumed
     * @note The insect can consume the spore and apply its effect.
     */
    public void consumeSpore(Spore spore) {
        spore.applyEffect(this);
        this.spores.add(spore);
    }

    /**
     * Check if the insect can consume the given spore.
     * @return true if the insect is under the effect if any spore, false otherwise
     * @note If the speed is normal and the cut is true, the is not under the effect of any spore.
     */
    public boolean coolDownCheck() {
        return this.speed == 2 && this.cut;
    }
}