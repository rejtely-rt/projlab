package fungorium.model;

import java.util.ArrayList;
import java.util.List;
import fungorium.tectons.*;
import fungorium.spores.*;
import fungorium.utils.*;;

public class Mushroom {
    private List<Spore> spores = new ArrayList<>();
    private List<Thread> threads = new ArrayList<>();
    private int level = 1; // Alapértelmezett szint
    private int life;      // Élettartam

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
     * Changes the internal state and logs the action.
     */
    public void evolve() {
        Logger.enter(this, "evolve");
        this.level = 2; // Szint növelése
        Logger.exit(null);
    }
    
    /**
     * Changes the life value of the mushroom.
     * Sets the internal life value and logs the action.
     * 
     * @param i The new life value.
     */
    public void changeLife(int i) {
        Logger.enter(this, "changeLife");
        this.life = i; // Élettartam beállítása
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
        List<Spore> sporeList = new ArrayList<>();
        Spore cspore = new CannotCutSpore();
        sporeList.add(cspore);
        Spore pspore = new ParalyzeSpore();
        sporeList.add(pspore);
        Spore spspore = new SpeedySpore();
        sporeList.add(spspore);
        Spore slspore = new SlowlySpore();
        sporeList.add(slspore);
        spores.addAll(sporeList);
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
            spores.clear();
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
            if (neighborMushroom != null && neighborMushroom.equals(this)) {
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
     * Collects and removes a full thread (or thread structure) starting from the
     * given Tecton.
     * This is a placeholder for a depth-first search (DFS) traversal
     * implementation.
     * 
     * @param t The starting Tecton.
     */
    public void threadCollector(Tecton startTecton) {
        // Step 1: Find the Tecton where the mushroom is located
        Tecton mushroomTecton = null;
        List<Tecton> visited = new ArrayList<>();
        List<Tecton> stack = new ArrayList<>();
        stack.add(startTecton);

        while (!stack.isEmpty()) {
            Tecton current = stack.remove(stack.size() - 1);
            if (visited.contains(current))
                continue;
            visited.add(current);

            // Check if the current Tecton contains this mushroom
            if (current.getMushroom() == this) {
                mushroomTecton = current;
            }

            // Add neighbors to the stack for further traversal
            List<Tecton> neighbors = current.getNeighbors();
            for (Tecton neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    stack.add(neighbor);
                }
            }
        }

        List<Tecton> allTectons = visited;

        // If the mushroom's Tecton is not found, exit
        if (mushroomTecton == null) {
            return;
        }

        // Step 2: Perform DFS from the mushroom's Tecton through the threads to build
        // the connected network
        visited.clear();
        stack.clear();
        stack.add(mushroomTecton);

        while (!stack.isEmpty()) {
            Tecton current = stack.remove(stack.size() - 1);
            if (visited.contains(current))
                continue;
            visited.add(current);

            // Add neighbors to the stack for further traversal
            List<Tecton> neighbors = current.getNeighbors();
            for (Tecton neighbor : neighbors) {
                List<Thread> neighborThreads = neighbor.getThreads();
                for (Thread neighborThread : neighborThreads) {
                    if (neighborThread.getParent() == this && current.getThreads().contains(neighborThread)) {
                        stack.add(neighbor);
                    }
                }
            }
        }

        allTectons.removeAll(visited);
        List<Thread> toRemove = new ArrayList<>();
        for (Tecton tecton : allTectons) {
            for (Thread thread : threads) {
                if (tecton.getThreads().contains(thread) && !thread.isKept()) {
                    toRemove.add(thread);
                    tecton.removeThread(thread);
                }
            }
        }
        threads.removeAll(toRemove);
    }
}
