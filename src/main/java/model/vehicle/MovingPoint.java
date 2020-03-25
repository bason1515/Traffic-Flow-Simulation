package model.vehicle;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

public class MovingPoint {
    private DoubleProperty x, y, xVelocity;
    private ReadOnlyDoubleWrapper speed;
    @Setter
    @Getter
    private Point2D direction;

    public MovingPoint(Point2D startingPoint, Point2D direction) {
        this(startingPoint, Point2D.ZERO, direction);
    }

    public MovingPoint(Point2D startingPoint, Point2D startingVelocity, Point2D direction) {
        this.x = new SimpleDoubleProperty(this, "x", startingPoint.getX());
        this.y = new SimpleDoubleProperty(this, "y", startingPoint.getY());
        this.xVelocity = new SimpleDoubleProperty(this, "xVelocity", startingVelocity.getX());
        this.speed = new ReadOnlyDoubleWrapper(this, "speed");
        this.direction = direction;
        applyBinds();
    }

    private void applyBinds() {
        speed.bind(Bindings.createDoubleBinding(this::getxVelocity, this.xVelocity));
    }

    public void applyVelocityToPosition(double elapsedSeconds) {
        double distTraveled = getSpeed() * 0.277 * elapsedSeconds;
        setPosition(getPosition().add(direction.multiply(distTraveled)));
    }

    public Point2D getPosition() {
        return new Point2D(this.getX(), this.getY());
    }

    public void setPosition(Point2D position) {
        this.setX(position.getX());
        this.setY(position.getY());
    }

    public double getVelocity() {
        return this.getxVelocity();
    }

    public void setVelocity(double v) {
        this.setxVelocity(v);
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

    public double getSpeed() {
        return speed.get();
    }

    public ReadOnlyDoubleWrapper speedProperty() {
        return speed;
    }

}
