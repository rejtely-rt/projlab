
package fungorium.model;
import fungorium.utils.Logger;

public class Thread {
    private Mushroom parent;
    
    public Mushroom getParent() {
        return parent;
    }

    public void setParent(Mushroom pMushroom) {
        parent = pMushroom;
    }

    public int getSize() {
        //return size;
        boolean isSizeHigh = Logger.question("A fonál elég magas szintű?");
        if (isSizeHigh) return 5;
        return 1;
    } 
    /* 
    public void changeSize(int value) {
        size+=value;
    } */
}
