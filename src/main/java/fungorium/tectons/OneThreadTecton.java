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
        return super.addThread(thread);
    }
}