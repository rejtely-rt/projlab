package fungorium.model;
import fungorium.utils.Interpreter;
import java.util.ArrayList;
import java.util.List;

public class Thread {

    private Mushroom parent;
    private int size;
    private boolean isKept;
    private boolean cutOff;
    private List<Insect> insects = new ArrayList<>(); // List to store eaten insects

    /**
     * Default constructor.
     * Logs the creation of the thread instance.
     */
    public Thread() {
        Interpreter.create(this); 
        this.size = 0;
        this.isKept = false;
        this.cutOff = false;
    }

    /**
     * Returns whether the thread is kept.
     * 
     * @return true if the thread is kept, false otherwise.
     */
    public boolean isKept() {
        return isKept;
    }

    /**
     * Sets the thread to be kept.
     * 
     * @param isKept true if the thread should be kept, false otherwise.
     */
    public void setKept(boolean isKept) {
        this.isKept = isKept;
    }

    /**
     * Returns whether the thread is cut off.
     * 
     * @return true if the thread is cut off, false otherwise.
     */
    public boolean isCutOff() {
        return cutOff;
    }

    /**
     * Sets the thread to be cut off.
     * 
     * @param cutOff true if the thread should be cut off, false otherwise.
     */
    public void setCutOff(boolean cutOff) {
        this.cutOff = cutOff;
    }

    /**
     * Returns the parent mushroom of this thread.
     * 
     * @return the {@link Mushroom} that owns this thread.
     */
    public Mushroom getParent() {
        return parent;
    }

    /**
     * Sets the parent mushroom for this thread.
     * 
     * @param pMushroom the {@link Mushroom} that owns this thread.
     * @throws IllegalArgumentException if the parent mushroom is null.
     */
    public void setParent(Mushroom pMushroom) {
        if (pMushroom == null) {
            throw new IllegalArgumentException("Parent mushroom cannot be null.");
        }
        parent = pMushroom;
    }

    /**
     * Returns the size of the thread.
     * This method asks the user interactively whether the thread is considered "high level",
     * and returns it value.
     * 
     * @return 5 if the thread is high-level, otherwise 1.
     */
    public int getSize() {
        return size;
    } 

    /**
     * Changes the size of the thread by adding the given value.
     * 
     * @param i the value to add to the current size.
     * @throws IllegalArgumentException if the resulting size would be negative.
     */
    public void changeSize(int i) {
        if (size + i < 0) {
            throw new IllegalArgumentException("Resulting size cannot be negative.");
        }
        size += i;
    }

    /**
     * Eats a paralyzed insect, causing it to die and adding it to this thread.
     * 
     * @param insect The insect to be eaten.
     */
    public void eatInsect(Insect insect) {
        if (insect.getSpeed() == 0) { // Check if the insect is paralyzed
            insect.setLife(false); // Set the insect's life to false
            insect.setLocation(null); // Remove the insect from its current location
            insects.add(insect); // Add the insect to this thread
        }
    }

    /**
     * Returns the list of insects associated with this thread.
     * 
     * @return the list of insects.
     */
    public List<Insect> getInsects() {
        return insects;
    }
}
