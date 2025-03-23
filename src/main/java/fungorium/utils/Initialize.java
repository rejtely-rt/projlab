package fungorium.utils;

import fungorium.model.*;
import fungorium.tectons.*;


public abstract class Initialize {
    // Common objects for tests
    protected Tecton t1;
    protected OneThreadTecton t2;
    protected NoMushTecton t3;
    protected ThreadAbsorberTecton t4;
    protected Mushroom m1;
    protected Insect i1;
    protected Insect i2;
    protected Insect i3;
    protected Insect i4;

    /**
     * The initialize method initializes the objects common to all tests.
     * This method should be called at the beginning of each test.
     */
    public void initialize() {
        t1 = new Tecton();
        t2 = new OneThreadTecton();
        t3 = new NoMushTecton();
        t4 = new ThreadAbsorberTecton();
        t1.addNeighbour(t2);
        t2.addNeighbour(t1);
        t2.addNeighbour(t3);
        t3.addNeighbour(t2);
        t3.addNeighbour(t4);
        t4.addNeighbour(t3);
        t4.addNeighbour(t1);
        t1.addNeighbour(t4);

        // t1.addMushroom();
        // m1 = t1.getMushroom();
        // m1.addThread(t1);
        // m1.addThread(t4);
        // m1.produceSpores();
        // m1.produceSpores();
        // m1.produceSpores();
        // m1.shootSpores(t2);
        // m1.shootSpores(t4);

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
