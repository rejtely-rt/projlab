package fungorium.utils;

import fungorium.model.*;
import fungorium.model.Thread;
import fungorium.tectons.*;


public abstract class Initialize {
    // Common objects for tests
    protected Tecton t1;
    protected NoMushTecton t2;
    protected ThreadAbsorberTecton t3;
    protected OneThreadTecton t4;
    protected Mushroom m1;
    protected Mushroom m2;
    protected Insect i1;
    protected Insect i2;
    protected Insect i3;
    protected Insect i4;
    protected Thread th1;
    protected Thread th2;    

    /**
     * The initialize method initializes the objects common to all tests.
     * This method should be called at the beginning of each test.
     */
    public void initialize() {
        m1 = new Mushroom();
        m2 = new Mushroom();

        t1 = new Tecton(m1);
        t2 = new NoMushTecton();
        t3 = new ThreadAbsorberTecton(m2);
        t4 = new OneThreadTecton();
        t1.addNeighbour(t2);
        t2.addNeighbour(t1);
        t2.addNeighbour(t3);
        t3.addNeighbour(t2);
        t3.addNeighbour(t4);
        t4.addNeighbour(t3);
        t4.addNeighbour(t1);
        t1.addNeighbour(t4);

        m1.addThread(t2);
        m1.addThread(t4);
        th1 = t2.getThreads().get(0);
        th2 = t4.getThreads().get(0);
        m1.produceSpores();
        m1.shootSpores(t2);
        m1.produceSpores();
        m1.shootSpores(t4);
        m1.produceSpores();

        i1 = new Insect();
        i2 = new Insect();
        i3 = new Insect();
        i4 = new Insect();
        i1.moveTo(t2);
        i2.moveTo(t2);
        i3.moveTo(t4);
        i4.moveTo(t4);
        i1.consumeSpore();
        i2.consumeSpore();
        i3.consumeSpore();
        i4.consumeSpore();
    }
}
