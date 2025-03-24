package fungorium.tectons;

import fungorium.utils.Logger;
import fungorium.model.Mushroom;
import fungorium.model.Thread;;

public class OneThreadTecton extends Tecton {
    public OneThreadTecton() {
    }   
    
    public OneThreadTecton(Mushroom m) {
        this.mushroom = m;
    }

    @Override
    public boolean addThread(Thread thread) {
        Logger.enter(this, "addThread");
        if (threads.size() == 1) {
            Logger.exit(false);
            return false;
        }
        boolean returnValue = super.addThread(thread);
        Logger.exit(returnValue);
        return returnValue;
    }
}