package model.car;

import javafx.geometry.Point2D;
import model.road.Road;

public class Obstacle extends Car {

    public Obstacle(Point2D position, Limitation carLimits, double width, double height, Road currentRoad) {
        super(position, carLimits, width, height, currentRoad);
    }

    @Override
    public void performDrive(double elapsedSeconds) {
    }
}
