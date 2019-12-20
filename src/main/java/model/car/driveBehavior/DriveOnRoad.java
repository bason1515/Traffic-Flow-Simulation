package model.car.driveBehavior;

import javafx.geometry.Point2D;
import model.car.Car;
import model.road.Road;

public class DriveOnRoad implements DriveStrategy {
    private static final int SAFE_GAP = 40;
    private Road drivenRoad;
    private Car myCar;
    private Car carInFront;

    public DriveOnRoad(Car myCar, Car carInFront) {
        this.myCar = myCar;
        this.carInFront = carInFront;
        this.drivenRoad = myCar.getCurrentRoad();
    }

    @Override
    public DriveStrategy driveCar(Car carInFront) {
        drive();
        return DriveStrategyDecider.getBestStrategy(myCar, carInFront);
    }

    private void drive() {
        if (carInFront == null || isSafeGap(SAFE_GAP))
            driveOnRoad(drivenRoad);
        else driveBehind();
    }

    private void stopCar() {
        myCar.slowDown(1);
    }

    private void driveOnRoad(Road target) {
        myCar.accelerate(target.getDirection());
    }

    private void driveBehind() {
        if (myCar.getSpeed() > carInFront.getSpeed() || !isSafeGap(SAFE_GAP - 5)) stopCar();
        else driveOnRoad(drivenRoad);
    }

    private boolean isSafeGap(double safeGap) {
        return distanceToCollision() > safeGap;
    }

    private double distanceToCollision() {
        Point2D carPos = myCar.getPosition();
        Point2D carInFrontPos = carInFront.getPosition();
        double distanceBetween = carPos.distance(carInFrontPos);
        double carsLength = (myCar.getHeight() / 2) + (carInFront.getHeight() / 2);
        return distanceBetween - carsLength;
    }
}
