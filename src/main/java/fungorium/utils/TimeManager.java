package fungorium.utils;

import java.util.List;

public class TimeManager {

    private List<Tickable> tickables;

    public TimeManager(List<Tickable> tickables) {
        this.tickables = tickables;
    }

    public void tickAll() {
        for (Tickable tickable : tickables) {
            tickable.tick();
        }
    }
}