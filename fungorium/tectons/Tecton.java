package fungorium.tectons;

import java.util.ArrayList;
import java.util.List;
import fungorium.model.Thread;
import fungorium.spores.Spore;
import fungorium.model.Mushroom;

public class Tecton {
    private List<Tecton> neighbors = new ArrayList<>();
    private List<Thread> threads = new ArrayList<>();
    private List<Spore> spores = new ArrayList<>();
    
    public void addNeighbour(Tecton t) {
        neighbors.add(t);
    }

    public List<Tecton> getNeighbors() {
        return new ArrayList<>(neighbors);
    }

    public void addMushroom() {
        // Implementation needed
    }

    public Mushroom getMushroom() {
        return null;
    }

    public void addThread(Thread thread) {
        threads.add(thread);
    }

    public List<Thread> getThreads() {
        return new ArrayList<>(threads);
    }

    public void removeThread(Thread t) {
        threads.remove(t);
    }

    public void addSpores(List<Spore> spores) {
        this.spores.addAll(spores);
    }

    public List<Spore> getSpores() {
        return new ArrayList<>(spores);
    }

    public void removeSpore(Spore s) {
        spores.remove(s);
    }

    public void absorbThread() {
        // Implementation needed
    }

    public void breakTecton() {
        // Implementation needed
    }
}
