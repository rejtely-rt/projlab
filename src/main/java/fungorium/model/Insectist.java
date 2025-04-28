package fungorium.model;

import java.util.ArrayList;
import java.util.List;
import fungorium.tectons.Tecton;

public class Insectist {
    /**
     * List of insects controlled by the Insectist.
     * This list contains all the insects that the Insectist can command and control.
     */
    private final List<Insect> insects;

    /**
     * Score of the Insectist.
     * This variable keeps track of the number of spores consumed by the Insectist's insects.
     * It is used to determine the Insectist's performance and progress in the game.
     */
    private int score;


    /**
     * Constructor for the Insectist class.
     * Initializes the list of insects controlled by the Insectist.
     */
    public Insectist() {
        this.insects = new ArrayList<>();
    }

    /**
     * Adds an insect to the Insectist's control.
     * @param insect the insect to be added
     */
    public void addInsect(Insect insect) {
        insects.add(insect);
    }

    /**
     * Removes an insect from the Insectist's control.
     * @param index the index of the insect to be removed
     */
    public void removeInsect(int index) {
        if (index >= 0 && index < insects.size()) {
            insects.remove(index);
        } else {
            System.out.println("Invalid insect index.");
        }
    }

    /**
     * Moves a specific insect to a new location.
     * @param index the index of the insect to be moved
     * @param target the target location
     */
    public void moveInsect(int index, Tecton target) {
        if (index >= 0 && index < insects.size()) {
            insects.get(index).moveTo(target);
        } else {
            System.out.println("Invalid insect index.");
        }
    }

    /**
     * Commands a specific insect to cut a thread.
     * @param index the index of the insect to perform the action
     * @param thread the thread to be cut
     */
    public void cutThread(int index, Thread thread) {
        if (index >= 0 && index < insects.size()) {
            if (!insects.get(index).cutThread(thread)){
                System.out.println("Insect could not cut the thread (regardless what the next line says)");
            }
        } else {
            System.out.println("Invalid insect index.");
        }
    }

    /**
     * Commands a specific insect to consume a spore.
     * @param index the index of the insect to perform the action
     */
    public void consumeSpore(int index) {
        if (index >= 0 && index < insects.size()) {
            insects.get(index).consumeSpore();
        } else {
            System.out.println("Invalid insect index.");
        }
    }

    /**
     * Adds 1 point to the Insectist's score.
     */
    public void addPoint() {
        score++;
    }

    /**
     * Gets the current score of the Insectist.
     * @return the current score
     */
    public int getScore() {
        return score;
    }

    /**
     * Gets the list of insects controlled by the Insectist.
     * @return the list of insects
     */
    public List<Insect> getInsects() {
        return insects;
    }
}
