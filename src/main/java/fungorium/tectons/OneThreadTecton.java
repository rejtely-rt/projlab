package fungorium.tectons;

import fungorium.utils.Logger;
import fungorium.model.Mushroom;
import fungorium.model.Thread;;

public class OneThreadTecton extends Tecton {
    public OneThreadTecton() {
        Logger.create(this);
    }   
    
    public OneThreadTecton(Mushroom m) {
        this.mushroom = m;
    }

    /**
     * Adds a thread to the tecton.
     * 
     * @param thread the thread to be added
     * @return {@code true} if the thread was added successfully, {@code false} otherwise
     * 
     * @note If the tecton already has
     * one thread, it will not add another and will return {@code false}.
     */
    @Override
    public boolean addThread(Thread thread) {
        Logger.enter(this, "addThread");
        if (threads.size() == 1) {
            Logger.exit(false);
            return false;
        }
        boolean returnValue = super.addThread(thread);
        Logger.exit(returnValue);
        return returnValue;
    }
}