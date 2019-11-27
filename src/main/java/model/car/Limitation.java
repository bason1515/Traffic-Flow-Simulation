package model.car;

import javafx.geometry.Point2D;
import lombok.Data;

@Data
public class Limitation {
    private double maxAccel;
    private double maxBreak;
    private double maxVel;

    public static Point2D limit(Point2D target, double limitation) {
        return target.normalize().multiply(limitation);
    }

    public Limitation(double maxAccel, double maxBreak, double maxVel) {
        this.maxAccel = maxAccel;
        this.maxBreak = maxBreak;
        this.maxVel = maxVel;
    }

    public Point2D limitWithMaxAccel(Point2D vector) {
        return limit(vector, maxAccel);
    }

    public Point2D limitWithMaxBreak(Point2D vector) {
        return limit(vector, maxBreak);
    }

    public Point2D limitWithMaxVelo(Point2D vector) {
        return limit(vector, maxVel);
    }

}
