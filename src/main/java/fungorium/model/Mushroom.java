

package fungorium.model;

import java.util.ArrayList;
import java.util.List;
import fungorium.tectons.*;
import fungorium.spores.*;
import fungorium.utils.*;;

public class Mushroom {
    private List<Spore> spores = new ArrayList<>();;
    private List<Thread> threads = new ArrayList<>();;
    
    public int getLevel() {
        Logger.enter(this, "getLevel");
        boolean isLevel1 = Logger.question("A gomba 1-es szintű?");
        int level = 0;
        Logger.exit(level);
        if (isLevel1) return level = 1;
        return level = 2;
        
        
    }
    
    public void evolve() {
        Logger.enter(this, "evolve");
        System.out.println("Itt hivatalosan 2-re változik a level, de a felhasználótól úgyis mindig megkérdezzük");
        Logger.exit(null);
    }
    
    public void changeLife(int i) {
        Logger.enter(this, "changeLife");
        System.out.println("Ezt még nem szükséges implementálni");
        Logger.exit(null);
    }
    
    public List<Thread> getThreads() {
        return threads; 
    }
    
    public void produceSpores() {
        Logger.enter(this, "produceSpores");
        Logger.exit(null);
    }
    
    /*szerintem itt kell egy olyan paraméter is, hogy milyen 
    (jobban mondva hány) spórát lőjön ki, mert most kilövi az egészet*/
    public void shootSpores(Tecton to) {
        boolean successfullShoot = to.addSpores(spores, this);
        if (successfullShoot) {
            for (Spore spore : spores) {
                spores.remove(spore);
            }
        }

    }

    
    
    public boolean addThread(Tecton t) {
        Logger.enter(this, "addThread");
        if (t instanceof OneThreadTecton && !t.getThreads().isEmpty()) {
            Logger.exit(false);
            return false;
        }
        List <Tecton> targetTectonNeighbors= t.getNeighbors();


        //ha szomszédos a tekton
        for (Tecton neighbor : targetTectonNeighbors) {
            Mushroom neighborMushroom = neighbor.getMushroom();
            // ha először a Mushroomot találtuk meg a Tekton szomszédjában
            if (neighborMushroom.equals(this)) {
                //hozzáadjuk a Mushroom threadjét
                Thread newThread = new Thread();
                newThread.setParent(this);
                t.addThread(newThread);
                neighbor.addThread(newThread);
                Logger.exit(true);
                return true;
            }

            // ha mondjuk épp a threadet találtuk meg a tekton szomszédjában
            List<Thread> neighborThreads = neighbor.getThreads();
            for (Thread neighborThread: neighborThreads) {
                if (neighborThread.getSize() < 5) continue;
                Mushroom parentMushroom = neighborThread.getParent();
                if (this.equals(parentMushroom)) {
                    Thread newThread = new Thread();
                    newThread.setParent(this);
                    t.addThread(newThread);
                    neighbor.addThread(newThread);
                    Logger.exit(true);
                    return true;
                }
            }

        }
        Logger.exit(false);
        return false; //ha nem találtuk meg a gombát
    }

    
    public void removeThread(Thread th) {
        Logger.enter(this, "removeThread");
        threads.remove(th); 
        Logger.exit(null);
    }
    

    //DFS implementálás
    public void threadCollector(Tecton t) {
        List<Tecton> neighborTectons = t.getNeighbors();

        for (Tecton neighborTecton :neighborTectons) {
            Mushroom foundMushroom = neighborTecton.getMushroom();
            
        }
    }
}
