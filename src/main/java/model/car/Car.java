package model.car;

import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import model.car.driveBehavior.Driver;
import model.road.Road;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class Car extends LimitedMovingPoint {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static Long count = 1L;
    @Setter(AccessLevel.NONE)
    private final Long carId;

    private CarType type;
    private Driver driver;
    private Road currentRoad;
    private Rectangle view;
    private Car carInFront;

    // Wiedemann driver dependent parameters
    @Setter(AccessLevel.NONE)
    private final double[] rnd = new double[4];

    public void performDrive(double elapsedSeconds) {
        driver.drive(carInFront, elapsedSeconds);
    }

    public Car(Point2D position, Limitation carLimits, double width, double height, Road currentRoad) {
        super(position, carLimits, currentRoad.getDirection());
        carId = count++;
        this.type = CarType.CAR;
        this.currentRoad = currentRoad;
        this.driver = new Driver(this);
        this.view = new Rectangle(getX(), getY(), width, height);
        bindings();
        wiedemannParam();
    }

    private void bindings() {
        view.xProperty().bind(Bindings.createDoubleBinding(() -> getX() - view.getWidth() / 2, xProperty()));
        view.yProperty().bind(Bindings.createDoubleBinding(() -> getY() - view.getHeight() / 2, yProperty()));
        view.rotateProperty().bind(Bindings.createDoubleBinding(this::calculateRotation, xVelocityProperty()));
    }

    private void wiedemannParam() {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        rnd[0] = rng.nextDouble() - 0.5;
        rnd[1] = rng.nextDouble(0, 0.4) - 0.2;
        rnd[2] = rng.nextDouble(0, 4) - 2;
        rnd[3] = rng.nextDouble();
    }

    private double calculateRotation() {
        Point2D direction = getDirection();
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

    public Optional<Car> getCarInFront() {
        return Optional.ofNullable(carInFront);
    }

    @Override
    public String toString() {
        return String.format("Car %d [%.0f , %.0f]", carId, getX(), getY());
    }

}