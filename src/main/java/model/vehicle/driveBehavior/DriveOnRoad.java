package model.vehicle.driveBehavior;

import javafx.geometry.Point2D;
import lombok.Setter;
import model.vehicle.Vehicle;
import model.road.Road;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class DriveOnRoad {
    private static ThreadLocalRandom rng = ThreadLocalRandom.current();
    @Setter
    private Road drivenRoad;
    private Vehicle myCar;
    private Vehicle carInFront;

    private double deltaX;
    private double deltaV;

    private static final double nrnd = rng.nextDouble();
    private double l;
    private double ax;
    private double abx;
    private double sdx;
    private double sdv;
    private double cldv;
    private double opdv;

    public DriveOnRoad(Vehicle myCar) {
        this.myCar = myCar;
        this.drivenRoad = myCar.getCurrentRoad();
    }

    public VehicleStatus getNewStatus(Vehicle carInFront) {
        this.carInFront = carInFront;
        return checkThresholds();
    }

    private VehicleStatus checkThresholds() {
        VehicleStatus newStatus = VehicleStatus.FREE;
        if (carInFront != null) {
            calculateParameters();
            if (deltaX < abx) {
                newStatus = VehicleStatus.BREAK;
                if (deltaX <= 0.5) {
                    newStatus = VehicleStatus.COLLISION;
                }
            } else if (deltaV > sdv) {
                newStatus = VehicleStatus.CLOSING_IN;
            } else if (deltaV < opdv || deltaX > sdx) {
                newStatus = VehicleStatus.FREE;
            } else {
                newStatus = VehicleStatus.FOLLOW;
            }
        }
        return newStatus;
    }

    private void calculateParameters() {
        deltaV = myCar.getSpeed() - carInFront.getSpeed();
        deltaX = calcDeltaX();
        wiedemannModel();
    }

    private double calcDeltaX() {
        Point2D carPos = myCar.getPosition();
        Point2D carInFrontPos = carInFront.getPosition();
        double distanceBetween = carPos.distance(carInFrontPos);
        double carsLength = (myCar.getHeight() / 2) + (carInFront.getHeight() / 2);
        return distanceBetween - carsLength;
    }

    private void wiedemannModel() {
        l = carInFront.getHeight() / 2;
        double v = Math.min(myCar.getSpeed(), carInFront.getSpeed());

        ax = 1;

        double bxAdd = 1.5 + myCar.getRnd()[0];
        double bx = bxAdd * Math.sqrt(v);
        abx = ax + bx;

        double ex = 2 + myCar.getRnd()[1];
        sdx = ax + ex * bx;

        double cx = 18 + myCar.getRnd()[2];
        sdv = Math.pow((deltaX - ax) / cx, 2);

        cldv = sdv * 1.1;

        opdv = cldv * -1.2;
    }

    public void drive() {
        VehicleStatus status = myCar.getDriver().getStatus();
        switch (status) {
            case FREE:
                freeDrive();
                break;
            case CLOSING_IN:
                decelerate();
                break;
            case BREAK:
                maxDeceleration();
                break;
            case COLLISION:
                myCar.setVelocity(0.0);
                break;
            default:
                break;
        }
    }

    private void maxDeceleration() {
        myCar.slowDown(myCar.getLimits().getMaxBreak());
    }

    private void decelerate() {
        if (Objects.isNull(carInFront)) return;
        myCar.setDirection(drivenRoad.getDirection());
        double timeToLeader = deltaX / (deltaV * 0.277);
        double breakForce = deltaV / timeToLeader;
        breakForce = -1 * Math.abs(breakForce);
        myCar.slowDown(Math.min(breakForce, -0.25));
    }

    private void freeDrive() {
        myCar.setDirection(drivenRoad.getDirection());
        myCar.accelerate();
    }

    @Override
    public String toString() {
        return ax + " |abx| " + abx + " |sdx| " + sdx + " |sdv| " + sdv + " |cldv| " +
                cldv + " |opdv| " + opdv + "|| dx: " + deltaX + " dv: " + deltaV;
    }

}