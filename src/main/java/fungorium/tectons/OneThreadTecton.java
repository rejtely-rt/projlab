package fungorium.tectons;

import fungorium.utils.Logger;
import fungorium.model.Thread;;

public class OneThreadTecton extends Tecton {
    public OneThreadTecton() {
        Logger.create(this);
    }   

    @Override
    public boolean addThread(Thread thread) {
        return super.addThread(thread);
    }
}