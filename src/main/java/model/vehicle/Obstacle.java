package model.vehicle;

import javafx.geometry.Point2D;
import model.road.Road;

public class Obstacle extends Vehicle {

    public Obstacle(Point2D position, Limitation carLimits, double width, double height, Road currentRoad) {
        super(position, carLimits, width, height, currentRoad);
        setType(VehicleType.OBSTACLE);
    }

    @Override
    public void performDrive(double elapsedSeconds) {
    }
}
