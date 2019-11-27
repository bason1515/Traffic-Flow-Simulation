package model.car;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;

public class MovingPoint {
    private DoubleProperty x, y, xVelocity, yVelocity;
    private ReadOnlyDoubleWrapper speed;

    public MovingPoint(Point2D startingPoint) {
        this(startingPoint, Point2D.ZERO);
    }

    public MovingPoint(Point2D startingPoint, Point2D startingVelocity) {
        this.x = new SimpleDoubleProperty(this, "x", startingPoint.getX());
        this.y = new SimpleDoubleProperty(this, "y", startingPoint.getY());
        this.xVelocity = new SimpleDoubleProperty(this, "xVelocity", startingVelocity.getX());
        this.yVelocity = new SimpleDoubleProperty(this, "yVelocity", startingVelocity.getY());
        this.speed = new ReadOnlyDoubleWrapper(this, "speed");
        applyBinds();
    }

    private void applyBinds() {
        speed.bind(Bindings.createDoubleBinding(() -> this.getVelocity().magnitude(), this.xVelocity, this.yVelocity));
    }

    public void applyVelocityToPosition(double elapsedSeconds) {
        Point2D distTraveled = getVelocity().multiply(elapsedSeconds);
        setPosition(getPosition().add(distTraveled));
    }

    public Point2D getPosition() {
        return new Point2D(this.getX(), this.getY());
    }

    public void setPosition(Point2D position) {
        this.setX(position.getX());
        this.setY(position.getY());
    }

    public Point2D getVelocity() {
        return new Point2D(this.getxVelocity(), this.getyVelocity());
    }

    public void setVelocity(Point2D position) {
        this.setxVelocity(position.getX());
        this.setyVelocity(position.getY());
    }

    public double getX() {
        return x.get();
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public void setX(double x) {
        this.x.set(x);
    }

    public double getY() {
        return y.get();
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public void setY(double y) {
        this.y.set(y);
    }

    public double getxVelocity() {
        return xVelocity.get();
    }

    public DoubleProperty xVelocityProperty() {
        return xVelocity;
    }

    public void setxVelocity(double xVelocity) {
        this.xVelocity.set(xVelocity);
    }

    public double getyVelocity() {
        return yVelocity.get();
    }

    public DoubleProperty yVelocityProperty() {
        return yVelocity;
    }

    public void setyVelocity(double yVelocity) {
        this.yVelocity.set(yVelocity);
    }

    public double getSpeed() {
        return speed.get();
    }

    public ReadOnlyDoubleWrapper speedProperty() {
        return speed;
    }

}
