package model.car.driveBehavior;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import model.car.Car;
import model.road.Road;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class Driver {
    @Setter(AccessLevel.NONE)
    private Car myCar;
    private Car carInFront;
    private DriveOnRoad driveOnRoad;
    private ChangeLane changeLane;
    private CarStatus status;
    private CarStatus desStatus;
    private double reactionTime;
    private double timeFromNewStatus;

    public Driver(Car myCar) {
        this.myCar = myCar;
        this.driveOnRoad = new DriveOnRoad(myCar);
        this.changeLane = new ChangeLane(myCar);
        this.status = CarStatus.FREE;
        reactionTime = 1.0 + ThreadLocalRandom.current().nextDouble(0.0,1.0);
        timeFromNewStatus = 0.0;
    }

    public void drive(Car carInFront, double elapsedSeconds) {
        this.carInFront = carInFront;
        checkForLaneChange();
        reaction(elapsedSeconds);
        driveOnRoad.drive();
        CarStatus.setCarColor(myCar);
    }

    private void reaction(double elapsedSeconds) {
        desStatus = driveOnRoad.getNewStatus(carInFront);
        if (desStatus == status) return;
        timeFromNewStatus += elapsedSeconds;
        System.out.println("time To react: " + timeFromNewStatus + " / " + reactionTime);
        if (timeFromNewStatus >= reactionTime) {
            System.out.println("Reaction from " + status + " to " + desStatus);
            status = desStatus;
            timeFromNewStatus = 0.0;
        }

    }

    private void checkForLaneChange() {
        if (changeLane.checkIfEnded()) {
            Optional<Road> rightRoad = myCar.getCurrentRoad().getRight();
            Optional<Road> leftRoad = myCar.getCurrentRoad().getLeft();
            if (changeLane.shouldChangeToRight()) {
                rightRoad.ifPresent(changeLane::initTransition);
            } else if (changeLane.shouldOvertake())
                leftRoad.ifPresent(changeLane::initTransition);
        }
    }

}
