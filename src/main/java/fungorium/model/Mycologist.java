package fungorium.model;

import java.util.ArrayList;
import java.util.List;
import fungorium.tectons.Tecton;

public class Mycologist {
    /**
     * List of mushrooms controlled by the Mycologist.
     * This list contains all the mushrooms that the Mycologist can command and control.
     */
    private final List<Mushroom> mushrooms;
    /**
     * Score of the Mycologist.
     * This variable keeps track of the number of spores consumed by the Mycologist's mushrooms.
     * It is used to determine the Mycologist's performance and progress in the game.
     */
    private int score;

    /**
     * Constructor for the Mycologist class.
     * Initializes the list of mushrooms controlled by the Mycologist.
     */
    public Mycologist() {
        this.mushrooms = new ArrayList<>();
        this.score = 0;
    }

    /**
     * Adds a mushroom to the Mycologist's control.
     * @param mushroom the mushroom to be added
     */
    public void addMushroom(Mushroom mushroom) {
        mushrooms.add(mushroom);
        addPoint(); // Award a point for successful mushroom creation
    }

    /**
     * Attempts to add a mushroom to a specific Tecton.
     * If successful, the mushroom is added to the Mycologist's control, and a point is awarded.
     * @param tecton the Tecton where the mushroom should be added
     */
    public void addMushroom(Tecton tecton) {
        if (tecton.addMushroom()) { // Calls the Tecton's addMushroom method
            Mushroom newMushroom = tecton.getMushroom(); // Retrieve the newly created mushroom
            mushrooms.add(newMushroom); // Add it to the Mycologist's list
            addPoint(); // Award a point for successful mushroom creation
        }
    }

    /**
     * Removes a mushroom from the Mycologist's control.
     * @param index the index of the mushroom to be removed
     */
    public void removeMushroom(int index) {
        if (index >= 0 && index < mushrooms.size()) {
            mushrooms.remove(index);
        } else {
            System.out.println("Invalid mushroom index.");
        }
    }

    /**
     * Commands a specific mushroom to shoot spores at a target location.
     * @param index the index of the mushroom to perform the action
     * @param target the target location for the spores
     */
    public void shootSpores(int index, Tecton target) {
        if (index >= 0 && index < mushrooms.size()) {
            mushrooms.get(index).shootSpores(target);
        } else {
            System.out.println("Invalid mushroom index.");
        }
    }

    /**
     * Commands a specific mushroom to grow a thread to a target location.
     *
     * @param index the index of the mushroom to perform the action
     * @param target the target location for the thread
     */
    public void addThread(int index, Tecton target) {
        if (index >= 0 && index < mushrooms.size()) {
            Mushroom mushroom = mushrooms.get(index);

            // A gomba megpróbál egy fonalat növeszteni a cél Tecton-ra
            boolean success = mushroom.addThread(target);

            if (success) {
                System.out.println("Thread successfully grown from mushroom " + index + " to target Tecton.");
            } else {
                System.out.println("Failed to grow thread to target Tecton.");
            }
        } else {
            System.out.println("Invalid mushroom index.");
        }
    }

    /**
     * Commands a specific thread to eat a paralyzed insect.
     *
     * @param thread The thread that will attempt to eat the insect.
     * @param insect The insect to be eaten.
     */
    public void eatInsect(Thread thread, Insect insect) {
        boolean success = thread.eatInsect(insect);
        if (success) {
            System.out.println("The Mycologist successfully commanded the thread to eat the insect.");
        } else {
            System.out.println("The Mycologist failed to command the thread to eat the insect.");
        }
    }

    /**
     * Adds 1 point to the Mycologist's score.
     */
    public void addPoint() {
        score++;
    }

    /**
     * Gets the current score of the Mycologist.
     * @return the current score
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Gets the list of mushrooms controlled by the Mycologist.
     * @return the list of mushrooms
     */
    public List<Mushroom> getMushrooms() {
        return mushrooms;
    }
}
