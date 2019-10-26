package model.car;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import model.road.Road;


@Getter
@Setter
public class Car {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static Long count = 1L;
    @Setter(AccessLevel.NONE)
    private final Long carId;

    Road currentRoad;
    //    Properties
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private DoubleProperty x, y, xVelocity, yVelocity;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final ReadOnlyDoubleWrapper speed;
//    private final ReadOnlyDoubleWrapper front; // angle

    //    Limitation
    private double maxAccel;
    private double maxBreak;
    private double maxVel;
    //    View
    private boolean transition;
    private Rectangle view;

    public Car(double x, double y, double width, double height, Road currentRoad, double maxAccel, double maxBreak, double maxVel) {
        carId = count++;
        this.maxAccel = maxAccel;
        this.maxBreak = maxBreak;
        this.maxVel = maxVel;
        this.currentRoad = currentRoad;
        this.view = new Rectangle(x, y, width, height);
        this.x = new SimpleDoubleProperty(this, "x", x);
        this.y = new SimpleDoubleProperty(this, "y", y);
        this.xVelocity = new SimpleDoubleProperty(this, "xVelocity", 0);
        this.yVelocity = new SimpleDoubleProperty(this, "yVelocity", 0);
        this.speed = new ReadOnlyDoubleWrapper(this, "speed");

//        Bindings
        speed.bind(Bindings.createDoubleBinding(() -> this.getVelocity().magnitude(), this.xVelocity, this.yVelocity));
        view.xProperty().bind(Bindings.createDoubleBinding(() -> getX() - view.getWidth() / 2, this.x));
        view.yProperty().bind(Bindings.createDoubleBinding(() -> getY() - view.getHeight() / 2, this.y));
        view.rotateProperty().bind(Bindings.createDoubleBinding(() -> {
            Point2D direction = this.getVelocity();
            if (direction.equals(Point2D.ZERO)) direction = currentRoad.getDirection();
            if (getxVelocity() < 0)
                return direction.angle(0, 1) + 180.0;
            return direction.angle(0, -1);
        }, this.xVelocity, this.yVelocity));
    }

//    Getters Setters

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

    public double getWidth() {
        return view.getWidth();
    }

    public void setWidth(double width) {
        view.setWidth(width);
    }

    public double getHeight() {
        return view.getHeight();
    }

    public void setHeight(double height) {
        view.setHeight(height);
    }

    public double getRotate() {
        return view.getHeight();
    }
}