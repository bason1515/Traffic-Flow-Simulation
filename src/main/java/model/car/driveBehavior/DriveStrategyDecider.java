package model.car.driveBehavior;

import javafx.geometry.Point2D;
import lombok.AccessLevel;
import lombok.Getter;
import model.car.Car;
import model.road.Road;

public class DriveStrategyDecider {

    private Car myCar;
    private Car carInFront;
    @Getter(AccessLevel.PRIVATE)
    private DriveStrategy strategy;

    public static DriveStrategy getBestStrategy(Car myCar, Car carInFront) {
        return new DriveStrategyDecider(myCar, carInFront).getStrategy();
    }

    private DriveStrategyDecider(Car myCar, Car carInFront) {
        this.myCar = myCar;
        this.carInFront = carInFront;
        this.strategy = findBestStrategy();
    }

    private DriveStrategy findBestStrategy() {
        Road rightRoad = myCar.getCurrentRoad().getRight();
        Road leftRoad = myCar.getCurrentRoad().getLeft();
        if (canChangeLine(rightRoad)) {
            return new ChangeLine(myCar, rightRoad);
        } else if (isSafeGap(40))
            return new DriveOnRoad(myCar, carInFront);
        else if (shouldOvertake())
            return new ChangeLine(myCar, leftRoad);
        else return new DriveOnRoad(myCar, carInFront);

    }

    private boolean shouldOvertake() {
        Road leftRoad = myCar.getCurrentRoad().getLeft();
        return canChangeLine(leftRoad) && myCar.getLimits().getMaxVel() > carInFront.getSpeed();
    }

    private boolean canChangeLine(Road target) {
        if (target == null) return false;
        boolean isThereACar = target.getOnRoad().stream()
                .anyMatch(c -> c.getPosition().distance(myCar.getPosition()) < 50);
        return !isThereACar;
    }

    private boolean isCarInFront() {
        return carInFront != null;
    }

    private boolean isSafeGap(double safeGap) {
        return !isCarInFront() || distanceToCollision() > safeGap;
    }

    private double distanceToCollision() {
        Point2D carPos = myCar.getPosition();
        Point2D carInFrontPos = carInFront.getPosition();
        double distanceBetween = carPos.distance(carInFrontPos);
        double carsLength = (myCar.getHeight() / 2) + (carInFront.getHeight() / 2);
        return distanceBetween - carsLength;
    }

}
