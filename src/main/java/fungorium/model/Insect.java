package fungorium.model;

import fungorium.spores.Spore;
import fungorium.tectons.Tecton;
import java.util.List;
import java.util.ArrayList;

import fungorium.utils.Interpreter;
import fungorium.utils.Tickable;

public class Insect implements Tickable{
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

    /**
     * Constructor of the Insect class.
     * It initializes the speed, cut, and spores fields with default values.
     * Also assigns the insect to the given Insectist.
     * @param insectist the Insectist to which this insect will be assigned
     */
    public Insect(Insectist insectist) {
        this.spores = new ArrayList<>();
        this.cut = true;
        this.speed = 2;
        Interpreter.create(this);
        insectist.addInsect(this); // Assign the insect to the Insectist
    }

    public void setLocation(Tecton location) {
        this.location = location;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setCut(boolean cut) {
        this.cut = cut;
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
    public List<Spore> getSpores() {
        return spores;
    }

    /**
     * Getter method for the life field.
     * @return the life of the insect
     */
    public boolean getLife() {
        return life;
    }

    /**
     * Setter method for the life field.
     * @param life the new life value
     */
    public void setLife(boolean life) {
        this.life = life;
    }

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
     */
    public void moveTo(Tecton target) {
        if (this.location == null) {
            this.location = target;
            return;
        }
        if (getSpeed() <= 0) {
            System.out.println("Insect is paralyzed, therefore cannot move.");
            return;
        }

        List<Thread> currentThreads = this.location.getThreads();
        List<Thread> targetThreads = target.getThreads();
        for (Thread thread : currentThreads) {
            if (targetThreads.contains(thread)) {
                this.location = target;
                return;
            }
        }
        System.out.println("Insect did not find a thread, therefore cannot move.");
    }

    /**
     * Cut the given thread.
     * @param thread the thread to be cut
     * @return true if the thread is cut, false otherwise
     */
    public boolean cutThread(Thread thread) {
        if (!getCut() || !location.getThreads().contains(thread)) {
            return false;
        }
        thread.setCutOff(true);
        return true;
    }

    /**
     * Consume the given spore.
     */
    public boolean consumeSpore() {
        List<Spore> sporeList = this.location.getSpores();
        if (sporeList.isEmpty()) {
            return false; // Nem volt spóra a helyszínen
        }
        Spore spore = sporeList.get(0);
        sporeList.remove(0);
        spore.applyEffect(this);
        this.spores.add(spore);
        return true; // Sikeres spórafogyasztás
    }

    /**
     * Check if the insect can consume the given spore.
     */
    public void coolDownCheck() {
        for (Spore spore : spores) {
            spore.decreaseCooldown();
            int cooldown = spore.getCooldown();
            if (cooldown == 0) {
                spore.removeEffect(this);
            }
        }
    }
    /**
     * Clone the insect.
     * @return a new insect with the same attributes
     */
    public Insect clone() {
        Insect clonedInsect = new Insect();
        clonedInsect.setLocation(this.location);
        clonedInsect.setSpeed(this.speed);
        clonedInsect.setCut(this.cut);
        clonedInsect.setLife(this.life);
        return clonedInsect;
    }

    @Override
    public void tick(){
        coolDownCheck();
    }

}