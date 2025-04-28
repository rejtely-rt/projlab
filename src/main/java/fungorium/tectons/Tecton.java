package fungorium.tectons;

import java.util.ArrayList;
import java.util.List;
import fungorium.model.Thread;
import fungorium.spores.Spore;
import fungorium.model.Mushroom;
import fungorium.utils.Interpreter;
import fungorium.utils.Tickable;
import fungorium.model.Insect;

public class Tecton implements Tickable {

    protected List<Tecton> neighbors = new ArrayList<>();
    protected List<Thread> threads = new ArrayList<>();
    protected List<Spore> spores = new ArrayList<>();
    protected Mushroom mushroom = null;

    public Tecton() {
        Interpreter.create(this);
    }

    /**
     * Adds a neighboring Tecton to this Tecton's list of neighbors.
     *
     * @param t the Tecton to be added as a neighbor
     */
    public void addNeighbour(Tecton t) {
        neighbors.add(t);
    }

    /**
     * Removes a neighboring Tecton from this Tecton's list of neighbors.
     *
     * @param t the Tecton to be removed from the list of neighbors
     */
    public void removeNeighbour(Tecton t) {
        neighbors.remove(t);
    }

    /**
     * Retrieves the list of neighboring Tecton objects.
     *
     * @return a list of Tecton objects that are neighbors to the current Tecton.
     */
    public List<Tecton> getNeighbors() {
        return new ArrayList<>(neighbors);
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
        if(threads.stream().anyMatch(thread -> !thread.getInsects().isEmpty())) {
            Mushroom m = new Mushroom(1);
            this.mushroom = m;
            System.out.println("   -> New mushroom created on Tecton by thread.");
            return true;
        }
        if (mushroom != null) {
            System.out.println("   -> Already has mushroom, can't add a new one.");
            return false;
        }
        if (spores.size() == 0) {
            System.out.println("   -> No spores, can't grow mushroom.");
            return false;
        }
        if (threads.size() == 0) {
            System.out.println("   -> No threads, can't grow mushroom.");
            return false;
        }
        Mushroom m = new Mushroom(1);
        this.mushroom = m;

        System.out.println("   -> New mushroom created on Tecton.");
        return true;
    }
    
    /**
     * Attempts to add a mushroom to the Tecton.
     * Only for testing purposes.
     * 
     * @param mushroom the Mushroom object to be added
     * @return true if the mushroom was successfully added, false otherwise.
     */
    public boolean addMushroom(Mushroom mushroom) {
        if (this.mushroom != null) {
            System.out.println("   -> Already has mushroom, can't add a new one.");
            return false;
        }
        this.mushroom = mushroom;
        //System.out.println("   -> New mushroom created on Tecton.");
        return true;
    }

    /**
     * Retrieves the Mushroom object associated with this Tecton.
     *
     * @return the Mushroom object associated with this Tecton.
     */
    public Mushroom getMushroom() {
        return mushroom;
    }

    /**
     * Adds a thread to the Tecton.
     *
     * @param thread the thread to be added
     * @return true if the thread was successfully added
     */
    public boolean addThread(Thread thread) {
        threads.add(thread);
        System.out.println("   -> 0 added to Tecton.");
        return true;
    }

    public void removeThread(Thread thread) {
        threads.remove(thread);
        System.out.println("   -> 0 removed from Tecton.");
    }

    /**
     * Retrieves a list of threads.
     * It returns a new ArrayList containing the threads.
     *
     * @return a list of threads.
     */
    public List<Thread> getThreads() {
        return new ArrayList<>(threads);
    }
    
    //for testing, because the ThreadCollector is not implemented
    public void removeThreadLocally(Thread t) {
        threads.remove(t);
    }    
    //--------------------------------------------

    public void forceAddSpores(List<Spore> spores) { //JUST FOR INITIALIZATION
        this.spores.addAll(spores);
    }

    /**
     * Adds spores to the current Tecton if the given mushroom is found in the neighboring Tectons.
     * If the mushroom's level is 2, it also checks the neighbors of the neighboring Tectons.
     *
     * @param spores the list of spores to be added
     * @param mushroom the mushroom to be checked in the neighboring Tectons
     * @return true if the spores were added, false otherwise
     */// LEVEL 1: közvetlen szomszédnál keresünk gombát
    public boolean addSpores(List<Spore> spores, Mushroom mushroom) {
        int mushLevel = mushroom.getLevel();
    
        if (mushLevel == 1) {
// LEVEL 1: közvetlen szomszédnál keresünk gombát
            for (Tecton neighbor : neighbors) {
                if (mushroom.equals(neighbor.getMushroom())) {
                    this.spores.addAll(spores);
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
                        return true;
                    }
                }
            }
        }
    
        return false;
    }    

    /**
     * Retrieves the list of spores associated with this Tecton.
     *
     * @return a list of Spore objects.
     */
    public List<Spore> getSpores() {
        return spores;
    }

    /**
     * Removes the specified spore from the collection of spores.
     *
     * @param s the spore to be removed
     */
    public void removeSpore(Spore s) {
        spores.remove(s);
    }

    public void absorbThread() {
        List<Thread> threadsCopy = new ArrayList<>(threads);
    
        for (Thread thread : threadsCopy) {
            if (!thread.isCutOff()) {
                continue;
            }
    
            thread.changeSize(-1);
    
            if (thread.getSize() == 0) {
                this.removeThread(thread);
    
                for (Tecton neighbor : this.getNeighbors()) {
                    if (neighbor.getThreads().contains(thread)) {
                        neighbor.removeThread(thread);
                    }
                }
    
                if (this.mushroom != null) {
                    this.mushroom.removeThread(thread);
                    this.mushroom.threadCollector(this);
                }
            }
        }
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
        List<Thread> threads = new ArrayList<>(this.threads);
        for (Thread thread : threads) {
            Mushroom m1 = thread.getParent();
            m1.removeThread(thread);
            this.removeThread(thread);
            m1.threadCollector(this);
        }
        Tecton t2 = new Tecton();
    
        for (Tecton neighbor : neighbors) {
            t2.addNeighbour(neighbor);         
            neighbor.addNeighbour(t2);     
        }
    
        this.addNeighbour(t2);
        t2.addNeighbour(this);

        return t2;
    }

    @Override
    public void tick() {
        absorbThread();
        breakTecton();
    }
}