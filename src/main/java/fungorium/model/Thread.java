package fungorium.model;

public class Thread {
    private Mushroom parent;
    private int size;
    
    public Mushroom getParent() {
        return parent;
    }
    public int getSize() {
        return size;
    }
    public void changeSize(int newSize) {
        // Implementation needed
    }
    public void decreaseSize() {
        this.changeSize(size - 1);
    }
    public void increaseSize() {
        this.changeSize(size + 1);
    }
}