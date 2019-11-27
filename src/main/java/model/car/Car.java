package model.car;

import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import model.car.driveBehavior.DriveOnRoad;
import model.car.driveBehavior.DriveStrategy;
import model.road.Road;

@Getter
@Setter
public class Car extends LimitedMovingPoint {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static Long count = 1L;
    @Setter(AccessLevel.NONE)
    private final Long carId;

    private DriveStrategy driver;
    private Road currentRoad;
    private Rectangle view;

    public void performDrive(Car carInFront) {
        driver = driver.driveCar(carInFront);
    }

    public Car(Point2D position, Limitation carLimits, double width, double height, Road currentRoad) {
        super(position, carLimits);
        carId = count++;
        this.currentRoad = currentRoad;
        this.driver = new DriveOnRoad(this, null);
        this.view = new Rectangle(getX(), getY(), width, height);
        bindings();
    }

    private void bindings() {
        view.xProperty().bind(Bindings.createDoubleBinding(() -> getX() - view.getWidth() / 2, xProperty()));
        view.yProperty().bind(Bindings.createDoubleBinding(() -> getY() - view.getHeight() / 2, yProperty()));
        view.rotateProperty().bind(Bindings.createDoubleBinding(this::calculateRotation, xVelocityProperty(), yVelocityProperty()));
    }

    private double calculateRotation() {
        Point2D direction = this.getVelocity();
        if (direction.equals(Point2D.ZERO)) direction = currentRoad.getDirection(); // TODO something better?
        // Point2D .angle doesn't distinguishes left or right
        if (getxVelocity() < 0)
            return direction.angle(0, 1) + 180.0;
        return direction.angle(0, -1);
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

    @Override
    public String toString() {
        return String.format("Car %d [%.0f , %.0f]", carId, getX(), getY());
    }
}