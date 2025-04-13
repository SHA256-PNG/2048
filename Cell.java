package TwentyFortyEight;

public class Cell {

    private int x;
    private int y;
    private int value;
    private boolean merged = false;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.value = 0;
    }

    public void place() {
        if (this.value == 0) {
            this.value = (App.random.nextInt(2)+1)*2;
        }
    }

    /**
     * This draws the cell
     */
    public void reset() {
        this.value = 0;
        this.merged = false;
    }

    public int getValue() {
        return this.value;
    }
    
    public void setValue(int val) {
        this.value = val;
    }

    public boolean canMergeWith(Cell other) {
        return this.value != 0 && this.value == other.value && !this.merged && !other.merged;
    }

    public void merge(Cell other) {
        if (this.canMergeWith(other)) {
            this.value *= 2;
            this.merged = true;
            other.setValue(0);
        }
    }

    public void clearMergedFlag() {
        this.merged = false;
    }

    public void draw(App app) {
        app.stroke(156, 139, 124);
            if (app.mouseX > x*App.CELLSIZE && app.mouseX < (x+1)*App.CELLSIZE 
                && app.mouseY > y*App.CELLSIZE && app.mouseY < (y+1)*App.CELLSIZE) {
                app.fill(232, 207, 184);
            } else if (this.value == 2) {
                app.image(app.eight, x*App.CELLSIZE, y*App.CELLSIZE, App.CELLSIZE, App.CELLSIZE);
            } else {
                app.fill(189, 172, 151);
            }
        app.rect(x*App.CELLSIZE, y*App.CELLSIZE, App.CELLSIZE, App.CELLSIZE);
        if (this.value > 0) {
            app.fill(0,0,0);
            app.text(String.valueOf(this.value), (x+0.4f)*App.CELLSIZE, (y+0.6f)*App.CELLSIZE);
        }
    }

}
