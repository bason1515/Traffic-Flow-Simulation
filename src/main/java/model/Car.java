package model;

import javafx.event.Event;
import javafx.event.EventHandler;
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

    Road currentRoad;
    Point2D velocity = Point2D.ZERO;
    Point2D position = Point2D.ZERO;
    Point2D accel = Point2D.ZERO;
    double maxAccel = 0.05;
    double maxBreak = 0.1;
    double maxVel = 1;

    public Car() {
        super();
        carId = count++;
    }

    public Car(double width, double height, Road road) {
        super(width, height);
        carId = count++;
        currentRoad = road;
        setX(road.getStartX());
        setY(road.getStartY());
        position = new Point2D(getX(), getY());
        setOnMouseEntered(new HoverListener());
    }

    public void drive() {
        if (currentRoad.getEndPoint2D().distance(position) < 5) {
            // getNewRoad, changeLine, stop, remove
            Road newRoad = currentRoad.getNextRoad();
            if (newRoad == null) stopCar();
            else {
                this.setCurrentRoad(newRoad);
                this.putAtTheBegin();
            }
        } else {
            Point2D force = currentRoad.getDriveDirection(position);

            accel = accel.add(force);
            accel = accel.normalize().multiply(maxAccel);

            velocity = velocity.add(accel);
            if (velocity.magnitude() >= maxVel)
                velocity = velocity.normalize().multiply(maxVel);
        }


        position = position.add(velocity);
//        System.out.println("Car position: " + position);

        // Update View
        setX(position.getX());
        setY(position.getY());
    }

    private void stopCar() {
        Point2D reversVelo = velocity.multiply(-1);
        reversVelo = reversVelo.normalize().multiply(maxBreak);
        if (reversVelo.magnitude() > velocity.magnitude()) {
            velocity = Point2D.ZERO;
        } else
            velocity = velocity.add(reversVelo);
    }

    public void putAtTheBegin() {
        setVelocity(Point2D.ZERO);
        setAccel(Point2D.ZERO);
        position = currentRoad.getStartPoint2D();
    }

    class HoverListener implements EventHandler {

        @Override
        public void handle(Event event) {
            displayInfo();
        }

        private void displayInfo() {
            System.out.printf("*** Car %d ***%nspeed: %.2f%nposition: %s%n",
                            getCarId(),
                            getVelocity().magnitude(),
                            getPosition());
        }
    }

    public void setPosition(Point2D position){
        this.position = position;
        setX(position.getX());
        setY(position.getY() );
    }
}