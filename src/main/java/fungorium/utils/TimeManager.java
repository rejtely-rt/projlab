package fungorium.utils;

import java.util.List;

/**
 * Manages a collection of Tickable objects and provides a method to tick all of them.
 */
public class TimeManager {

    private List<Tickable> tickables;

    /**
     * Constructs a TimeManager with the specified list of Tickable objects.
     *
     * @param tickables the list of Tickable objects to manage
     */
    public TimeManager(List<Tickable> tickables) {
        this.tickables = tickables;
    }

    /**
     * Calls the tick() method on all managed Tickable objects.
     */
    public void tickAll() {
        for (Tickable tickable : tickables) {
            tickable.tick();
        }
    }
}