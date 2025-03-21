package fungorium.model;

import fungorium.spores.Spore;
import fungorium.tectons.Tecton;
import fungorium.model.Thread;
import java.util.List;

public class Insect {
    private int speed;
    private boolean cut;
    private Tecton location;

    public Insect() {
        this.speed = 2;
        this.cut = true;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean getCut() {
        return cut;
    }

    public Tecton getLocation() {
        return location;
    }

    List<Spore> getScore() {
        // Implementation needed
        return null; // Example return value
    }
    
    public void changeSpeed(int value) {
        this.speed = value;
    }
    
    public void changeCut(boolean value) {
        this.cut = value;
    }
    
    public boolean moveTo(Tecton target) {
        if (this.speed == 0) {
            return false; // If the speed is 0, the insect cannot move
        }

        if (this.location == null || target == null) {
            return false; // If the current or target Tecton is null, the insect cannot move
        }

        // Current and target Tecton threads
        List<Thread> currentThreads = this.location.getThreads();
        List<Thread> targetThreads = target.getThreads();

        for (Thread thread : currentThreads) {
            if (targetThreads.contains(thread)) {   // If there is a connecting thread
                this.location = target; // Successful move
                return true;
            }
        }

        return false; // If there is no connecting thread, the insect cannot move
    }
    
    public boolean cutThread(Thread thread) {
        if (!this.cut) {
            return false; // If the insect cannot cut, the thread cannot be cut
        }


        // The parent mushroom of the thread
        Mushroom parentMushroom = thread.getParent();
        if (parentMushroom != null) {
            parentMushroom.removeThread(thread);
        }

        // If the thread is cut, the insect cannot move
        if (this.location != null) {
            List<Tecton> neighbors = this.location.getNeighbors();
            for (Tecton t : neighbors) {
                if (t.getThreads().isEmpty()) { // If the Tecton has no threads
                    assert parentMushroom != null;
                    parentMushroom.threadCollector(t);
                }
            }
        }
        return true;
    }

    public void consumeSpore(Spore spore) {
        spore.applyEffect(this);
    }
    
    public boolean coolDownCheck() {
        // Implementation needed
        return false; // Example return value
    }
}