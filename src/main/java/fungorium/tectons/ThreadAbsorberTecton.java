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

    /**
     * Absorbs threads from the current Tecton. This method iterates through a copy of the 
     * threads list to avoid ConcurrentModificationException. For each thread, it decreases 
     * its size by 1. If the size of the thread becomes 0, the thread is removed from the 
     * current Tecton and all its neighboring Tectons. Additionally, the thread is removed 
     * from its parent Mushroom, and the parent Mushroom's thread collector is updated.
     * 
     * This method logs entry and exit points for debugging purposes.
     */
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
            }
        }
        Logger.exit("");
   }
}
