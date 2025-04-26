package fungorium.model;

import java.util.ArrayList;
import java.util.List;
import fungorium.tectons.Tecton;

public class Mycologist {
    private final List<Mushroom> mushrooms;

    /**
     * Constructor for the Mycologist class.
     * Initializes the list of mushrooms controlled by the Mycologist.
     */
    public Mycologist() {
        this.mushrooms = new ArrayList<>();
    }

    /**
     * Adds a mushroom to the Mycologist's control.
     * @param mushroom the mushroom to be added
     */
    public void addMushroom(Mushroom mushroom) {
        mushrooms.add(mushroom);
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
     * @param index the index of the mushroom to perform the action
     * @param target the target location for the thread
     */
    public void growThread(int index, Tecton target) {
        if (index >= 0 && index < mushrooms.size()) {
            boolean success = mushrooms.get(index).addThread(target);
            if (success) {
                System.out.println("Thread successfully grown.");
            } else {
                System.out.println("Failed to grow thread.");
            }
        } else {
            System.out.println("Invalid mushroom index.");
        }
    }

    /**
     * Gets the list of mushrooms controlled by the Mycologist.
     * @return the list of mushrooms
     */
    public List<Mushroom> getMushrooms() {
        return mushrooms;
    }
}
