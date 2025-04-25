package fungorium.model;
import fungorium.utils.Logger;

public class Thread {

    private Mushroom parent;
    private int size;
    private boolean isKept;
    private boolean cutOff;

    /**
     * Default constructor.
     * Logs the creation of the thread instance.
     */
    public Thread() {
        Logger.create(this); 
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
        Logger.enter(this, "isKept");
        Logger.exit(isKept);
        return isKept;
    }

    /**
     * Sets the thread to be kept.
     * 
     * @param isKept true if the thread should be kept, false otherwise.
     */
    public void setKept(boolean isKept) {
        Logger.enter(this, "setKept");
        this.isKept = isKept;
        Logger.exit(null);
    }

    /**
     * Returns whether the thread is cut off.
     * 
     * @return true if the thread is cut off, false otherwise.
     */
    public boolean isCutOff() {
        Logger.enter(this, "isCutOff");
        Logger.exit(cutOff);
        return cutOff;
    }

    /**
     * Sets the thread to be cut off.
     * 
     * @param cutOff true if the thread should be cut off, false otherwise.
     */
    public void setCutOff(boolean cutOff) {
        Logger.enter(this, "setCutOff");
        this.cutOff = cutOff;
        Logger.exit(null);
    }

    /**
     * Returns the parent mushroom of this thread.
     * 
     * @return the {@link Mushroom} that owns this thread.
     */
    public Mushroom getParent() {
        Logger.enter(this, "getParent");
        Logger.exit(parent);
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
        Logger.enter(this, "setParent");
        parent = pMushroom;
        Logger.exit(null);
    }

    /**
     * Returns the size of the thread.
     * This method asks the user interactively whether the thread is considered "high level",
     * and returns it value.
     * 
     * @return 5 if the thread is high-level, otherwise 1.
     */
    public int getSize() {
        Logger.enter(this, "getSize");
        int size = Logger.questionNumber("Milyen vastag a fonál?");
        Logger.exit(size);
        return size;
    } 

    /**
     * Changes the size of the thread by adding the given value.
     * 
     * @param i the value to add to the current size.
     * @throws IllegalArgumentException if the resulting size would be negative.
     */
    public void changeSize(int i) {
        Logger.enter(this, "changeSize");
        if (size + i < 0) {
            throw new IllegalArgumentException("Resulting size cannot be negative.");
        }
        size += i;
        Logger.exit(null);
    }

    /**
     * Eats a paralyzed insect, causing it to die.
     * 
     * @param insect The insect to be eaten.
     */
    public void eatInsect(Insect insect) {
        Logger.enter(this, "eatInsect");
        if (insect.getSpeed() == 0) { // Check if the insect is paralyzed
            insect.setLife(false); // Set the insect's life to false
        }
        Logger.exit(null);
    }
}
