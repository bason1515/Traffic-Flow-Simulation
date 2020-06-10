package model.vehicle.driveBehavior;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import model.road.Road;
import model.vehicle.Vehicle;
import model.vehicle.changeLaneBehavior.ChangeLane;
import model.vehicle.changeLaneBehavior.ChangeLaneFactory;

import java.util.Optional;

@Getter
@Setter
public class Driver {
    @Setter(AccessLevel.NONE)
    private Vehicle myCar;
    private Vehicle carInFront;
    private Wiedemann wiedemann;
    private ChangeLane changeLane;
    private VehicleStatus status;
    private VehicleStatus desStatus;
    private double reactionTime;
    private double timeFromNewStatus;

    public Driver(Vehicle myCar) {
        this.myCar = myCar;
        this.wiedemann = new Wiedemann(myCar);
        this.changeLane = ChangeLaneFactory.createChangeLane(myCar);
        this.status = VehicleStatus.FREE;
        reactionTime = 0.5;
        timeFromNewStatus = 0.0;
    }

    public void drive(Vehicle carInFront, double elapsedSeconds) {
        this.carInFront = carInFront;
        changeLaneIfCan();
        updateDriveStatus();
        wiedemann.drive();
    }

    private void updateDriveStatus(){
        status = wiedemann.getNewStatus(carInFront);
        VehicleStatus.setCarColor(myCar);
    }

    private void reaction(double elapsedSeconds) {
        desStatus = wiedemann.getNewStatus(carInFront);
        if (desStatus == status) return;
        timeFromNewStatus += elapsedSeconds;
        if (timeFromNewStatus >= reactionTime) {
            status = desStatus;
            timeFromNewStatus = 0.0;
        }

    }

    private void changeLaneIfCan() {
        if (changeLane.checkIfEnded()) {
            Optional<Road> rightRoad = myCar.getCurrentRoad().getRight();
            Optional<Road> leftRoad = myCar.getCurrentRoad().getLeft();
            if (changeLane.shouldChangeToRight()) {
                rightRoad.ifPresent(changeLane::initTransition);
            } else if (changeLane.shouldChangeToLeft())
                leftRoad.ifPresent(changeLane::initTransition);
        }
    }

}
