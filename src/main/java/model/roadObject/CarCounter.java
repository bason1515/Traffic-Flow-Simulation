package model.roadObject;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import lombok.Getter;
import lombok.Setter;
import model.car.Car;
import model.road.Road;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CarCounter {
    double timeFromLastRefresh;
    double elapsedSeconds;
    int refreshSec;
    int totalCar;
    int totalSpeed;
    Point2D start;
    Point2D end;
    Line view;

    public CarCounter(Road road) {
        List<Road> monitoredRoads = road.getAllLines();
        Road firstRoad = monitoredRoads.get(0);
        Road lastRoad = monitoredRoads.get(monitoredRoads.size() - 1);
        start = firstRoad.getStartPoint2D().midpoint(firstRoad.getEndPoint2D());
        end = lastRoad.getStartPoint2D().midpoint(lastRoad.getEndPoint2D());
        offsetCounterLine();
        view = new Line(start.getX(), start.getY(),
                end.getX(), end.getY());
        refreshSec = 5;
    }

    private void offsetCounterLine() {
        Point2D size = start.subtract(end);
        start = start.add(size);
        end = end.add(size.multiply(-1));
    }

    public void update(Collection<Car> cars, double elapsedSeconds) {
        this.elapsedSeconds = elapsedSeconds;
        List<Car> crossedCar = cars.stream().filter(this::isCarCrossingCounter)
                .collect(Collectors.toList());
        addToAverage(crossedCar);
        timeFromLastRefresh += elapsedSeconds;
        if (timeFromLastRefresh > refreshSec) {
            calculateAverage();
            resetCounter();
        }
    }

    private boolean isCarCrossingCounter(Car car) {
        Point2D distanceTraveled = car.getVelocity().multiply(elapsedSeconds);
        return linesIntersect(
                car.getPosition(),
                car.getPosition().add(distanceTraveled),
                start,
                end);
    }

    private boolean linesIntersect(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
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

    private void addToAverage(List<Car> crossedCar) {
        crossedCar.forEach(c -> totalSpeed += c.getSpeed());
        totalCar += crossedCar.size();
    }

    private void calculateAverage() {
        if (totalCar != 0) {
            int avgSpeed = totalSpeed / totalCar;
            int throughputPerMinute = 60 / refreshSec * totalCar;
            System.out.println("Avg Speed: " + avgSpeed + "\nCar/min: " + throughputPerMinute);
        } else
            System.out.println("Avg Speed: " + 0 + "\nCar/min: " + 0);
    }

    public void resetCounter() {
        timeFromLastRefresh = 0;
        totalCar = 0;
        totalSpeed = 0;
    }

}
