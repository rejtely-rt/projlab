

package fungorium.model;

import java.util.ArrayList;
import java.util.List;
import fungorium.tectons.*;
import fungorium.spores.*;
import fungorium.utils.*;;

public class Mushroom {
    private List<Spore> spores = new ArrayList<>();
    private List<Thread> threads = new ArrayList<>();


    /**
     * Default constructor.
     * Logs the creation of the mushroom using the Logger.
     */
    public Mushroom() {
        Logger.create(this); 
    }
    
    /**
     * Asks the user to determine the level of the mushroom.
     * 
     * @return 1 if the user answers yes, 2 otherwise.
     */
    public int getLevel() {
        Logger.enter(this, "getLevel");
        int level = Logger.questionNumber("Hányas szintű a gomba?");
        Logger.exit(level);
        return level;
        
        
    }

    /**
     * Evolves the mushroom to level 2.
     * Currently does not change internal state, only logs the action.
     */
    public void evolve() {
        Logger.enter(this, "evolve");
        System.out.println("Itt hivatalosan 2-re változik a level, de a felhasználótól úgyis mindig megkérdezzük");
        Logger.exit(null);
    }
    
    /**
     * Changes the life value of the mushroom.
     * Currently only logs the call, no effect on internal state.
     * 
     * @param i The new life value (currently unused).
     */
    public void changeLife(int i) {
        Logger.enter(this, "changeLife");
        System.out.println("Ezt még nem szükséges implementálni");
        Logger.exit(null);
    }
    

    /**
     * Returns the list of threads belonging to this mushroom.
     * 
     * @return A list of Thread instances.
     */
    public List<Thread> getThreads() {
        return threads; 
    }
    
    /**
     * Produces spores.
     * Currently only logs the action without actual implementation.
     */
    public void produceSpores() {
        Logger.enter(this, "produceSpores");
        Logger.exit(null);
    }
    
    
     /**
     * Shoots spores at a given Tecton.
     * If successful, removes the shot spores from the internal list.
     * 
     * @param to The target Tecton.
     */
    public void shootSpores(Tecton to) {
        boolean successfullShoot = to.addSpores(spores, this);
        if (successfullShoot) {
            for (Spore spore : spores) {
                spores.remove(spore);
            }
        }

    }

    /**
     * Attempts to add a thread from this mushroom to a given Tecton.
     * It checks the neighbors of the target Tecton for this mushroom or an existing thread.
     * 
     * @param t The target Tecton.
     * @return {@code true} if the thread was successfully added; {@code false} otherwise.
     */
    public boolean addThread(Tecton t) {
        Logger.enter(this, "addThread");
    
        List <Tecton> targetTectonNeighbors= t.getNeighbors();


        //The tecton is a neighbor
        for (Tecton neighbor : targetTectonNeighbors) {
            Mushroom neighborMushroom = neighbor.getMushroom();
            // If Mushroom is found first
            if (neighborMushroom.equals(this)) {
                Thread newThread = new Thread();
                boolean tSuccessfullyAdded = t.addThread(newThread);
                if (!tSuccessfullyAdded) return false;
                newThread.setParent(this);
                boolean neighborSuccessfullyAdded = neighbor.addThread(newThread);
                if (!neighborSuccessfullyAdded) return false;
                threads.add(newThread);
                Logger.exit(true);
                return true;
            }

            // The thread is next to the tecton
            List<Thread> neighborThreads = neighbor.getThreads();
            for (Thread neighborThread: neighborThreads) {
                if (neighborThread.getSize() < 5) continue;
                Mushroom parentMushroom = neighborThread.getParent();
                if (this.equals(parentMushroom)) {
                    Thread newThread = new Thread();
                    boolean successfullyAdded = t.addThread(newThread);
                    if (!successfullyAdded) return false;
                    newThread.setParent(this);
                    boolean neighborSuccessfullyAdded = neighbor.addThread(newThread);
                    if (!neighborSuccessfullyAdded) return false;
                    threads.add(newThread);
                    Logger.exit(true);
                    return true;
                }
            }

        }
        Logger.exit(false);
        return false; //Mushroom was not found
    }

    /**
     * Removes a thread from this mushroom.
     * 
     * @param th The thread to be removed.
     */
    public void removeThread(Thread th) {
        Logger.enter(this, "removeThread");
        threads.remove(th); 
        Logger.exit(null);
    }
    

    /**
     * Collects and removes a full thread (or thread structure) starting from the given Tecton.
     * This is a placeholder for a depth-first search (DFS) traversal implementation.
     * 
     * @param t The starting Tecton.
     */
    public void threadCollector(Tecton t) {
        Logger.enter(this, "threadCollector");
        System.out.println("Egy teljes fonalat törlünk");
        Logger.exit(null);
    }
}
