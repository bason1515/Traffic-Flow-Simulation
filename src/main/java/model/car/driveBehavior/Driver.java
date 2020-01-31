package model.car.driveBehavior;

import lombok.Getter;
import model.car.Car;
import model.road.Road;

import java.util.Optional;

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
        Optional<Road> rightRoad = myCar.getCurrentRoad().getRight();
        Optional<Road> leftRoad = myCar.getCurrentRoad().getLeft();
        if (changeLane.shouldChangeToRight()) {
            rightRoad.ifPresent(changeLane::initTransition);
        } else if (changeLane.shouldOvertake())
            leftRoad.ifPresent(changeLane::initTransition);
        driveOnRoad.drive(carInFront);
    }

}
