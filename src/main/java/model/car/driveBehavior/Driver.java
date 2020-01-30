package model.car.driveBehavior;

import javafx.geometry.Point2D;
import lombok.Getter;
import model.car.Car;
import model.road.Road;

@Getter
public class Driver {
    private Car myCar;
    private Car carInFront;
    private DriveOnRoad driveOnRoad;
    private ChangeLane changeLane;

    public Driver(Car myCar) {
        this.myCar = myCar;
        this.driveOnRoad = new DriveOnRoad(myCar);
        this.changeLane = new ChangeLane(myCar);
    }

    public void drive(Car carInFront) {
        this.carInFront = carInFront;
        findBestStrategy();
    }

    private void findBestStrategy() {
        if (!changeLane.checkIfEnded()) driveOnRoad.drive(carInFront);
        Road rightRoad = myCar.getCurrentRoad().getRight();
        Road leftRoad = myCar.getCurrentRoad().getLeft();
        CarStatus status = driveOnRoad.getStatus();
        if (changeLane.shouldChangeToRight()) {
            changeLane.initTransition(rightRoad);
        } else if (changeLane.shouldOvertake())
            changeLane.initTransition(leftRoad);
        driveOnRoad.drive(carInFront);
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
