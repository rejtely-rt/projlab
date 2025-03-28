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

    /**
     * Adds a neighboring Tecton to this Tecton's list of neighbors.
     *
     * @param t the Tecton to be added as a neighbor
     */
    public void addNeighbour(Tecton t) {
        Logger.enter(this, "addNeighbour");
        neighbors.add(t);
        Logger.exit("");
    }

    /**
     * Retrieves the list of neighboring Tecton objects.
     *
     * @return a list of Tecton objects that are neighbors to the current Tecton.
     */
    public List<Tecton> getNeighbors() {
        Logger.enter(this, "getNeighbors");
        List<Tecton> result = new ArrayList<>(neighbors);
        Logger.exit(result); 
        return result;
    }

    /**
     * Attempts to add a mushroom to the Tecton.
     * 
     * If all checks pass, a new Mushroom object is created, assigned to the Tecton,
     * and a success message is logged. The method then returns true.
     * 
     * @return true if a new mushroom was successfully added, false otherwise.
     */
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

    /**
     * Retrieves the Mushroom object associated with this Tecton.
     *
     * @return the Mushroom object associated with this Tecton.
     */
    public Mushroom getMushroom() {
        Logger.enter(this, "getMushroom");
        Logger.exit(mushroom);
        return mushroom;
    }

    /**
     * Adds a thread to the Tecton.
     *
     * @param thread the thread to be added
     * @return true if the thread was successfully added
     */
    public boolean addThread(Thread thread) {
        Logger.enter(this, "addThread");
        threads.add(thread);
        System.out.println("   -> 0 added to Tecton.");
        Logger.exit(true);
        return true;
    }

    /**
     * Retrieves a list of threads.
     * It returns a new ArrayList containing the threads.
     *
     * @return a list of threads.
     */
    public List<Thread> getThreads() {
        Logger.enter(this, "getThreads");
        List<Thread> result = new ArrayList<>(threads);
        Logger.exit(result);
        return result;
    }

    /**
     * Removes the specified thread from the list of threads.
     *
     * @param t the thread to be removed
     */
    public void removeThread(Thread t) {
        Logger.enter(this, "removeThread");
        threads.remove(t);
        for (Tecton neighbor : neighbors) {
            if (neighbor.getThreads().contains(t)) {
                neighbor.removeThreadLocally(t); // új metódus, csak a belső listát módosítja

            }
        }
        Logger.exit("");
    }
    //for testing, because the ThreadCollector is not implemented
    public void removeThreadLocally(Thread t) {
        threads.remove(t);
    }    
    //--------------------------------------------

    public void forceAddSpores(List<Spore> spores) { //JUST FOR INITIALIZATION
        Logger.enter(this, "forceAddSpores");
        this.spores.addAll(spores);
        Logger.exit(true);
    }
    /**
     * Adds spores to the current Tecton if the given mushroom is found in the neighboring Tectons.
     * If the mushroom's level is 2, it also checks the neighbors of the neighboring Tectons.
     *
     * @param spores the list of spores to be added
     * @param mushroom the mushroom to be checked in the neighboring Tectons
     * @return true if the spores were added, false otherwise
     */
    public boolean addSpores(List<Spore> spores, Mushroom mushroom) {
        Logger.enter(this, "addSpores");
    
        int mushLevel = mushroom.getLevel();
    
        if (mushLevel == 1) {
            // LEVEL 1: közvetlen szomszédnál keresünk gombát
            for (Tecton neighbor : neighbors) {
                if (mushroom.equals(neighbor.getMushroom())) {
                    this.spores.addAll(spores);
                    Logger.exit(true);
                    return true;
                }
            }
        }
    
        if (mushLevel == 2) {
            // LEVEL 2: szomszéd szomszédjánál keresünk gombát
            for (Tecton neighbor : neighbors) {
                List<Tecton> neighborsOfNeighbor = neighbor.getNeighbors();
                for (Tecton n2 : neighborsOfNeighbor) {
                    if (mushroom.equals(n2.getMushroom())) {
                        this.spores.addAll(spores);
                        Logger.exit(true);
                        return true;
                    }
                }
            }
        }
    
        Logger.exit(false);
        return false;
    }    
    

    /**
     * Retrieves the list of spores associated with this Tecton.
     *
     * @return a list of Spore objects.
     */
    public List<Spore> getSpores() {
        Logger.enter(this, "getSpores");
        Logger.exit(spores);
        return spores;
    }

    /**
     * Removes the specified spore from the collection of spores.
     *
     * @param s the spore to be removed
     */
    public void removeSpore(Spore s) {
        Logger.enter(this, "removeSpore");
        spores.remove(s);
        Logger.exit("");
    }

    public void absorbThread() {
        Logger.enter(this, "absorbThread");    
        Logger.exit("");
    }

    /**
     * Breaks the current Tecton into two by creating a new Tecton and 
     * redistributing the neighbors. The new Tecton will have the same 
     * neighbors as the current one, and both Tectons will be neighbors 
     * to each other. All threads will be removed.
     *
     * @return the newly created Tecton
     */
    public Tecton breakTecton() {
        Logger.enter(this, "breakTecton");
        List<Thread> threads = new ArrayList<>(this.threads);
        for (Thread thread : threads) {
            Mushroom m1 = thread.getParent();
            m1.removeThread(thread);
            this.removeThread(thread);
            m1.threadCollector(this);
        }
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

    
        Logger.exit(t2);
        return t2;
    }
}