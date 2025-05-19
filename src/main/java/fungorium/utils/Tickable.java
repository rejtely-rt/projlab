package fungorium.utils;

/**
 * Represents an object that can perform an action on each tick or time step.
 * Classes implementing this interface should define the behavior that occurs
 * when the {@code tick()} method is called, typically as part of a game loop
 * or simulation update cycle.
 */
public interface Tickable {
    void tick();
}
