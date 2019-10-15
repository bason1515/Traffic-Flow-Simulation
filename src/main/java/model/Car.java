package model;

import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
public class Car extends Rectangle implements Movable {
    static Long count = 1L;
    @ToString.Exclude
    private final Long carId;

    Drivable currentRoad;
    Point2D velocity = Point2D.ZERO;
    Point2D position = Point2D.ZERO;
    Point2D accel = Point2D.ZERO;
    double maxAccel = 0.1;
    double maxBreak = 0.5;
    double maxVel = 0.1;

    public Car() {
        super();
        carId = count++;
    }

    public Car(double x, double y, double width, double height, Road road) {
        super(x, y, width, height);
        carId = count++;
        position = new Point2D(x, y);
        currentRoad = road;
    }

    @Override
    public void drive(Drivable on) {
        Point2D force = on.getDriveDirection(position);

        accel = accel.add(force);
        accel = accel.normalize().multiply(maxAccel);

        velocity = velocity.add(accel);
        if (velocity.magnitude() >= maxVel)
            velocity = velocity.normalize().multiply(maxVel);

        position = position.add(velocity);
        System.out.println("Car position: " + position);

        // Update View
        setX(position.getX());
        setY(position.getY());
    }

}