package fungorium.tectons;

import java.util.ArrayList;
import java.util.List;
import fungorium.model.Thread;
import fungorium.spores.Spore;
import fungorium.model.Mushroom;
import fungorium.utils.Logger;

public class Tecton {

    protected List<Tecton> neighbors = new ArrayList<>();
    protected List<Thread> threads = new ArrayList<>();
    protected List<Spore> spores = new ArrayList<>();
    protected Mushroom mushroom = null;

    public Tecton() {
        // Skeleton: a létrehozásnál bejegyezzük magunkat a loggerbe
        Logger.create(this);
    }

    public Tecton(Mushroom m) {
        Logger.create(this);
        this.mushroom = m;
    }

    public void addNeighbour(Tecton t) {
        Logger.enter(this, "addNeighbour");
        neighbors.add(t);
        Logger.exit("");
    }
    public List<Tecton> getNeighbors() {
        Logger.enter(this, "getNeighbors");
        List<Tecton> result = new ArrayList<>(neighbors);
        Logger.exit(result); 
        return result;
    }

    public boolean addMushroom() {
        Logger.enter(this, "addMushroom");
        if (mushroom != null) {
            System.out.println("   -> Already has mushroom, can't add a new one.");
            Logger.exit(false);
            return false;
        }
        if (spores.size() == 0) {
            System.out.println("   -> No spores, can't grow mushroom.");
            Logger.exit(false);
            return false;
        }
        if (threads.size() == 0) {
            System.out.println("   -> No threads, can't grow mushroom.");
            Logger.exit(false);
            return false;
        }
        Mushroom m = new Mushroom();
        this.mushroom = m;
        System.out.println("   -> New mushroom created on Tecton.");
        Logger.exit(true);
        return true;
    }

    public Mushroom getMushroom() {
        Logger.enter(this, "getMushroom");
        Logger.exit(mushroom);
        return mushroom;
    }

    public boolean addThread(Thread thread) {
        Logger.enter(this, "addThread");
        threads.add(thread);
        System.out.println("   -> 0 added to Tecton.");
        Logger.exit(true);
        return true;
    }

    public List<Thread> getThreads() {
        Logger.enter(this, "getThreads");
        List<Thread> result = new ArrayList<>(threads);
        Logger.exit(result);
        return result;
    }

    public void removeThread(Thread t) {
        Logger.enter(this, "removeThread");
        threads.remove(t);
        Logger.exit("");
    }

    public boolean addSpores(List<Spore> spores, Mushroom mushroom) {
        int mushLevel = mushroom.getLevel();
        for (Tecton neighborTecton: neighbors) {
            if (mushroom.equals(neighborTecton.getMushroom())) {
                for (Spore spore : spores ) {
                    this.spores.add(spore);
                }
                return true;
            }

            if (mushLevel ==2) {
                List<Tecton> neighborNeigborTectons = neighborTecton.getNeighbors();
                for (Tecton neighborNeigborTecton : neighborNeigborTectons) {
                    if (mushroom.equals(neighborNeigborTecton.getMushroom())) {
                        for (Spore spore : spores ) {
                            this.spores.add(spore);
                        }
                        return true;
                    }
                } 
            }
        }
        return false;

    }
    

    public List<Spore> getSpores() {
        Logger.enter(this, "getSpores");
        Logger.exit(spores);
        return spores;
    }

    public void removeSpore(Spore s) {
        Logger.enter(this, "removeSpore");
        spores.remove(s);
        Logger.exit("");
    }


    public void absorbThread() {
        Logger.enter(this, "absorbThread");    
        Logger.exit("");
    }
    

    public Tecton breakTecton() {
        Logger.enter(this, "breakTecton");
    
        // Új Tecton létrehozása
        Tecton t2 = new Tecton();
    
        // Minden szomszédot hozzáadunk az újhoz
        for (Tecton neighbor : neighbors) {
            t2.addNeighbour(neighbor);         
            neighbor.addNeighbour(t2);     
        }
    
        // A két tekton egymás szomszédja lesz
        this.addNeighbour(t2);
        t2.addNeighbour(this);
        // A szálakat eltávolítjuk a tectonokról
    
        Logger.exit(t2);
        return t2;
    }
}