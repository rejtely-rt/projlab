package fungorium.tectons;

import fungorium.model.Mushroom;
import fungorium.model.Thread;;

public class OneThreadTecton extends Tecton {
    public OneThreadTecton() {
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
        if (threads.size() == 1) {
            return false;
        }
        boolean returnValue = super.addThread(thread);
        return returnValue;
    }
}