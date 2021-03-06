package model.roadObject;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
import lombok.Getter;
import lombok.Setter;
import model.road.Road;
import model.vehicle.Vehicle;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class VehicleCounter {
    private double timeFromLastRefresh;
    private double elapsedSeconds;
    private int refreshSec;
    private int totalCar;
    private int totalSpeed;
    private int throughputPerHour;
    private int avgSpeed;
    private Point2D start, end;
    private Line line;
    private Label label;
    private Group view;

    public VehicleCounter(Road road, double position) {
        refreshSec = 15;
        view = new Group();
        position = Math.min(1.0, position);
        position = Math.max(0.0, position);
        initLane(road, position);
        initLabel();
    }

    private void initLane(Road road, double position) {
        List<Road> monitoredRoads = road.getAllLanes();
        Road firstRoad = monitoredRoads.get(0);
        Road lastRoad = monitoredRoads.get(monitoredRoads.size() - 1);
        start = firstRoad.getStartPoint2D().add(firstRoad.getDirection().multiply(firstRoad.getLength() * position));
        end = lastRoad.getStartPoint2D().add(lastRoad.getDirection().multiply(lastRoad.getLength() * position));
        offsetCounterLane();
        line = new Line(start.getX(), start.getY(),
                end.getX(), end.getY());
        view.getChildren().add(line);
    }

    private void offsetCounterLane() {
        Point2D size = start.subtract(end);
        start = start.add(size);
        end = end.add(size.multiply(-1));
    }

    private void initLabel() {
        label = new Label("Car/h: " + 0 + "\nAvg Speed: " + 0);
        label.setLayoutX(end.getX() + 25);
        label.setLayoutY(end.getY() - 25);
        view.getChildren().add(label);
    }

    public void update(Collection<Vehicle> cars, double elapsedSeconds) {
        this.elapsedSeconds = elapsedSeconds;
        List<Vehicle> crossedCar = cars.stream().filter(this::isCarCrossingCounter)
                .collect(Collectors.toList());
        addToAverage(crossedCar);
        timeFromLastRefresh += elapsedSeconds;
        if (timeFromLastRefresh > refreshSec) {
            calculateAverageAndUpdateLabel();
            resetCounter();
        }
    }

    private boolean isCarCrossingCounter(Vehicle car) {
        Point2D distanceTraveled = car.getDirection().multiply(car.getVelocity() * 0.277 * elapsedSeconds);
        return lanesIntersect(
                car.getPosition(),
                car.getPosition().add(distanceTraveled),
                start,
                end);
    }

    private boolean lanesIntersect(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();

        double x3 = p3.getX();
        double y3 = p3.getY();
        double x4 = p4.getX();
        double y4 = p4.getY();

        double den = ((x1 - x2) * (y3 - y4)) - ((y1 - y2) * (x3 - x4));
        if (den == 0) return false;
        double t = (((x1 - x3) * (y3 - y4)) - ((y1 - y3) * (x3 - x4))) / den;
//        double u = (((x1 - x2) * (y1 - y3)) - ((y1 - y2) * (x1 - x3))) / den;
        return t >= 0.0 && t <= 1.0;
    }

    private void addToAverage(List<Vehicle> crossedCar) {
        crossedCar.forEach(c -> totalSpeed += c.getSpeed());
        totalCar += crossedCar.size();
    }

    private void calculateAverageAndUpdateLabel() {
        avgSpeed = totalCar == 0 ? 0 : totalSpeed / totalCar;
        throughputPerHour = 3600 / refreshSec * totalCar;
        label.setText("Car/h: " + throughputPerHour + "\nAvg Speed: " + avgSpeed);
    }

    public void resetCounter() {
        timeFromLastRefresh = 0;
        totalCar = 0;
        totalSpeed = 0;
    }

    public void reset() {
        resetCounter();
        calculateAverageAndUpdateLabel();
    }
}
