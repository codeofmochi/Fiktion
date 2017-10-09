package ch.epfl.sweng.fiktion;

/**
 * Created by pedro on 09/10/17.
 */

public class Position {
    private final double x,y;
    Position(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public double x() {
        return x;
    }
    public double y() {
        return y;
    }
    public double dist(Position that) {
        double diffX = x - that.x;
        double diffY = y - that.y;
        return Math.sqrt(diffX*diffX + diffY*diffY);
    }
    @Override
    public String toString() {
        return "(" + x + ',' + y + ")";
    }
}
