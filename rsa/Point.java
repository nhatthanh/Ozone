package rsa;

import java.io.Serializable;

public class Point implements Serializable {
    private static final long serialVersionUID = 1L;
    private double x = 0.0D;
    private double y = 0.0D;

    public Point(double var1, double var3) {
        this.x = var1;
        this.y = var3;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}
