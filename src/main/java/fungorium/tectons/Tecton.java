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

    public boolean addSpores(List<Spore> spores, Mushroom mushroom) {
        int mushLevel = mushroom.getLevel();
        for (Tecton neighborTecton: neighbors) {
            if (mushroom.equals(neighborTecton.getMushroom())) {
                for (Spore spore : spores ) {
                    spores.add(spore);
                }
                return true;
            }

            if (mushLevel ==2) {
                List<Tecton> neighborNeigborTectons = neighborTecton.getNeighbors();
                for (Tecton neighborNeigborTecton : neighborNeigborTectons) {
                    if (mushroom.equals(neighborNeigborTecton.getMushroom())) {
                        for (Spore spore : spores ) {
                            spores.add(spore);
                        }
                        return true;
                    }
                } 
            }
        }
        return false;

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
