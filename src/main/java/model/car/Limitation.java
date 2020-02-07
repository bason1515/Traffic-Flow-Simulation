package model.car;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ThreadLocalRandom;

public class Limitation {
    private DoubleProperty maxAccel;
    private DoubleProperty maxBreak;
    private DoubleProperty maxVel;
    @Getter
    @Setter
    private double vrand;

    public static Point2D limit(Point2D target, double limitation) {
        return target.normalize().multiply(limitation);
    }

    public Limitation(double maxAccel, double maxBreak, double maxVel) {
        vrand = ThreadLocalRandom.current().nextDouble(-5.0, 5.0);
        this.maxAccel = new SimpleDoubleProperty(this, "maxAccel", maxAccel);
        this.maxBreak = new SimpleDoubleProperty(this, "maxBreak", maxBreak);
        this.maxVel = new SimpleDoubleProperty(this, "maxVel", maxVel);
    }

    public Limitation(Limitation limitation){
        vrand = ThreadLocalRandom.current().nextDouble(-5.0, 5.0);
        this.maxAccel = limitation.maxAccelProperty();
        this.maxBreak = limitation.maxBreakProperty();
        this.maxVel = limitation.maxVelProperty();
    }

    public Point2D limitWithMaxAccel(Point2D vector) {
        return limit(vector, getMaxAccel());
    }

    public Point2D limitWithMaxBreak(Point2D vector) {
        return limit(vector, getMaxBreak());
    }

    public Point2D limitWithMaxVelo(Point2D vector) {
        return limit(vector, getMaxVel());
    }

    public double getMaxAccel() {
        return maxAccel.get();
    }

    public DoubleProperty maxAccelProperty() {
        return maxAccel;
    }

    public void setMaxAccel(double maxAccel) {
        this.maxAccel.set(maxAccel);
    }

    public double getMaxBreak() {
        return maxBreak.get();
    }

    public DoubleProperty maxBreakProperty() {
        return maxBreak;
    }

    public void setMaxBreak(double maxBreak) {
        this.maxBreak.set(maxBreak);
    }

    public double getMaxVel() {
        return maxVel.get() + vrand;
    }

    public DoubleProperty maxVelProperty() {
        return maxVel;
    }

    public void setMaxVel(double maxVel) {
        this.maxVel.set(maxVel);
    }
}
