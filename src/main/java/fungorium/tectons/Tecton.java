package fungorium.tectons;

import java.util.ArrayList;
import java.util.List;
import fungorium.model.Thread;
import fungorium.spores.Spore;
import fungorium.model.Mushroom;
import fungorium.utils.Logger;

public class Tecton {

    private List<Tecton> neighbors = new ArrayList<>();
    private List<Thread> threads = new ArrayList<>();
    private List<Spore> spores = new ArrayList<>();
    private Mushroom mushroom = null;

    public Tecton() {
        // Skeleton: a létrehozásnál bejegyezzük magunkat a loggerbe
        Logger.create(this);
    }

    public void addNeighbour(Tecton t) {
        Logger.enter(this, "addNeighbour");

        // Skeleton: eldöntjük, akarjuk-e ténylegesen hozzáadni?
        boolean shouldAdd = Logger.question("Should we add this Tecton as a neighbor?");
        if (shouldAdd) {
            neighbors.add(t);
            System.out.println("   -> Neighbor added.");
        } else {
            System.out.println("   -> User chose NOT to add neighbor.");
        }

        Logger.exit("");
    }

    public List<Tecton> getNeighbors() {
        Logger.enter(this, "getNeighbors");
        // Egyszerűen visszaadjuk, de a skeleton kód miatt lehetne user input is
        List<Tecton> result = new ArrayList<>(neighbors);
        Logger.exit(result); 
        return result;
    }

    /**
     * addMushroom: Skeleton-verzió:
     *  1) Megkérdezi a felhasználót, van-e már gomba ezen a Tectonon
     *  2) Ha van, akkor false-szal tér vissza
     *  3) Ha nincs, megkérdezi, vannak-e spórák
     *  4) Ha nincs spóra, false
     *  5) Egyébként létrehoz egy gombát és true-t ad vissza
     */
    public boolean addMushroom() {
        Logger.enter(this, "addMushroom");
        boolean alreadyHasMushroom = Logger.question("Does this Tecton already have a Mushroom?");
        if (alreadyHasMushroom) {
            System.out.println("   -> Already has mushroom, can't add a new one.");
            Logger.exit(false);
            return false;
        }
        boolean hasSpores = Logger.question("Does this Tecton have at least one spore?");
        if (!hasSpores) {
            System.out.println("   -> No spores, can't grow mushroom.");
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

    /**
     * addThread: Skeleton-verzió:
     *  1) Megkérdezi a loggerrel, szabad-e ide fonalat rakni
     *  2) Ha user szerint nem, akkor false
     *  3) Ha igen, threads.add(...)
     */
    public boolean addThread(Thread thread) {
        Logger.enter(this, "addThread");
        boolean canAdd = Logger.question("Is it allowed to add this thread to Tecton?");
        if (!canAdd) {
            System.out.println("   -> Thread addition refused by user input.");
            Logger.exit(false);
            return false;
        }
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
        boolean doRemove = Logger.question("Should we remove this thread?");
        if (doRemove) {
            threads.remove(t);
            System.out.println("   -> Thread removed from Tecton.");
        } else {
            System.out.println("   -> Removal cancelled by user.");
        }
        Logger.exit("");
    }

    /**
     * addSpores: skeleton-megvalósítás
     *  - eldöntjük, tényleg hozzáadjuk-e őket
     *  - a paraméterek itt illusztratív jellegűek
     */
    public boolean addSpores(List<Spore> sporeList) {
        Logger.enter(this, "addSpores");
        boolean shallAdd = Logger.question("Shall we add these spores to Tecton?");
        if (shallAdd) {
            spores.addAll(sporeList);
            System.out.println("   -> Spores added to Tecton.");
            Logger.exit(true);
            return true;
        } else {
            System.out.println("   -> Spores not added.");
            Logger.exit(false);
            return false;
        }
    }

    public List<Spore> getSpores() {
        Logger.enter(this, "getSpores");
        List<Spore> result = new ArrayList<>(spores);
        Logger.exit(result);
        return result;
    }

    public void removeSpore(Spore s) {
        Logger.enter(this, "removeSpore");
        boolean doRemove = Logger.question("Should we remove this spore from Tecton?");
        if (doRemove) {
            spores.remove(s);
            System.out.println("   -> Spore removed.");
        } else {
            System.out.println("   -> Not removed, user cancelled.");
        }
        Logger.exit("");
    }


    public void absorbThread() {
        Logger.enter(this, "absorbThread");
    
        // Másolat, hogy elkerüljük ConcurrentModificationException-t
        List<Thread> threadsCopy = new ArrayList<>(threads);
    
        for (Thread th : threadsCopy) {
            th.decreaseSize();
    
            int size = th.getSize();
            Logger.enter(th, "getSize");
            Logger.exit(size);
    
            if (size == 0) {
                // Tecton eltávolítja magából a szálat
                this.removeThread(th);
    
                // Minden szomszéd Tectonból is eltávolítjuk a szálat
                for (Tecton neighbor : neighbors) {
                    List<Thread> neighborThreads = neighbor.getThreads();
                    if (neighborThreads.contains(th)) {
                        neighbor.removeThread(th);
                        neighbor.getMushroom().threadCollector(this);
                    }
                }
            }
        }
    
        Logger.exit("");
    }
    

    public void breakTecton() {
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
    
        this.absorbThread();
        t2.absorbThread();
    
        Logger.exit("");
    }
    
}