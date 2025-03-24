package fungorium.tectons;

import java.util.ArrayList;
import java.util.List;

import fungorium.model.Mushroom;
import fungorium.utils.Logger;
import fungorium.model.Thread;

public class ThreadAbsorberTecton extends Tecton {
    public ThreadAbsorberTecton() {
    }
    
    public ThreadAbsorberTecton(Mushroom m) {
        this.mushroom = m;
    }

    @Override 
    public void absorbThread(){
        Logger.enter(this, "absorbThread");
        // Másolat, hogy elkerüljük ConcurrentModificationException-t
        List<Thread> threadsCopy = new ArrayList<>(threads);
    
        for (Thread th : threadsCopy) {
            th.changeSize(-1);
    
            int size = th.getSize();    
            if (size == 0) {
                // Tecton eltávolítja magából a szálat
                this.removeThread(th);
    
                // Minden szomszéd Tectonból is eltávolítjuk a szálat
                for (Tecton neighbor : neighbors) {
                    List<Thread> neighborThreads = neighbor.getThreads();
                    if (neighborThreads.contains(th)) {
                        neighbor.removeThread(th);
                        Mushroom m1 = th.getParent();
                        m1.removeThread(th);
                        threads.remove(th);
                        m1.threadCollector(this);
                    }
                }
            }
        }
        Logger.exit("");
   }
}
