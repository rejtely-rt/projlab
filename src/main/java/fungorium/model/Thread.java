
package fungorium.model;
import fungorium.utils.Logger;

public class Thread {

    
    private Mushroom parent;

    /**
     * Default constructor.
     * Logs the creation of the thread instance.
     */
    public Thread() {
        Logger.create(this); 
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
     */
    public void setParent(Mushroom pMushroom) {
        Logger.enter(this, "setParent");
        parent = pMushroom;
        Logger.exit(null);
    }

     /**
     * Returns the size of the thread.
     * This method asks the user interactively whether the thread is considered "high level",
     * and returns 5 if so, or 1 otherwise.
     * 
     * @return 5 if the thread is high-level, otherwise 1.
     */
    public int getSize() {
        Logger.enter(this, "getSize");
        int size = Logger.question("A fonál elég magas szintű?") ? 5 : 1;
        Logger.exit(size);
        return size;
    } 

    /**
     * Changes the size of the thread.
     * Implementation is currently not provided.
     */
    public void changeSize(int size) {
        Logger.enter(this, "changeSize");
        System.out.println("Size is changed by value");
        Logger.exit(null);
    }
}
