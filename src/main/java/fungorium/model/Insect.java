package fungorium.model;

import fungorium.spores.Spore;
import fungorium.tectons.Tecton;
import fungorium.model.Thread;
import java.util.List;
import java.util.ArrayList;
import fungorium.utils.Logger;



public class Insect {
    public enum Player {
        INSESCIST,
        MYCOLOGIST,
    }
    /**
     * It is the current location of the insect.
     */
    private Tecton location;

    //life or dead
    /**
     * It is the life of the insect.
     * If the insect is dead, it cannot move or consume spores.
     * If the insect is alive, it can move and consume spores.
     * @note The insect can be dead if a thread eat it, when the insect was paralyzed!
     */
    private boolean life;
    
    /**
     * Spores is defined by a list of Spore objects.
     * It is the spores that the insect has consumed.
     */
    private final List<Spore> spores;

    /**
     * Determines if the insect can cut threads.
     * Default value is true.
     */
    private boolean cut = true;

    /**
     * Represents the speed of the insect.
     * Default value is 2.
     */
    private int speed = 2;

    private Player owner;

    public Player getOwner() {
        Logger.enter(this, "getOwner");
        Logger.exit(owner);
        return owner;
    }

    public void setOwner(Player owner) {
        Logger.enter(this, "setOwner");
        this.owner = owner;
        Logger.exit("");
    }

    public void setLocation(Tecton location) {
        Logger.enter(this, "setLocation");
        this.location = location;
        Logger.exit("");
    }

    public void setSpeed(int speed) {
        Logger.enter(this, "setSpeed");
        this.speed = speed; // Hiányzó hozzárendelés
        Logger.exit("");
    }

    public void setCut(boolean cut) {
        Logger.enter(this, "setCut");
        this.cut = cut; // Hiányzó hozzárendelés
        Logger.exit("");
    }

    /**
     * Constructor of the Insect class.
     * It initializes the speed, cut and spores fields.
     */
    public Insect() {
        this.spores = new ArrayList<>();
        Logger.create(this);
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
    List<Spore> getScore() { return spores; }

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
        this.speed += value; // Hiányzó logika
        Logger.exit("");
    }

    /**
     * Change the cut of the insect.
     * @param value the new cut value
     */
    public void changeCut(boolean value) {
        Logger.enter(this, "changeCut");
        this.cut = value; // Hiányzó logika
        Logger.exit("");
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
        if (this.location == null) {
            this.location = target;
            Logger.exit(false);
            return;
        }
        if (getSpeed() <= 0) { // Ellenőrzés a sebességre
            Logger.exit(false);
            return;
        }

        // További logika a mozgás távolságának ellenőrzésére, ha szükséges
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
     * @note The insect can cut the thread and remove it from the parent mushroom.
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
     * @note The insect can consume the spore and apply its effect.
     */
    public void consumeSpore(){
        Logger.enter(this, "consumeSpore");
        List<Spore> sporeList = this.location.getSpores();
        if (sporeList.isEmpty()){
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
     * @note If the speed is normal and the cut is true, the is not under the effect of any spore.
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