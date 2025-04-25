package fungorium.tectons;

import fungorium.model.Thread;

public class ThreadKeeperTecton extends Tecton {

    /**
     * Adds a thread to this Tecton and marks it as kept.
     * 
     * @param thread The thread to be added.
     * @return {@code true} if the thread was successfully added; {@code false} otherwise.
     */
    @Override
    public boolean addThread(Thread thread) {
        boolean added = super.addThread(thread); // Call the parent class's addThread method
        if (added) {
            thread.setKept(true); // Mark the thread as kept
        }
        return added;
    }
}
