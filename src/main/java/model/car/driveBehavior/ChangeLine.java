package model.car.driveBehavior;

import javafx.geometry.Point2D;
import model.car.Car;
import model.road.Road;

public class ChangeLine implements DriveStrategy {
    private Road target;
    private Road transition;
    private Car myCar;
    private boolean ended = false;

    public ChangeLine(Car myCar, Road target) {
        this.myCar = myCar;
        this.target = target;
        initTransition();
    }

    private void initTransition() {
        createTransition();
        changeRoad();
    }

    private void createTransition() {
        Point2D position = myCar.getPosition();
        double distance = myCar.getCurrentRoad().getStartPoint2D().subtract(position).magnitude();
        Point2D targetPoint = target.getPointOnLine(distance + myCar.getSpeed());
        transition = new Road(position, targetPoint);
    }

    private void changeRoad() {
        myCar.getCurrentRoad().removeOnRoad(myCar);
        target.addOnRoad(myCar);
        myCar.setCurrentRoad(target);
        myCar.setVelocity(transition.getDirection().multiply(myCar.getSpeed()));
    }

    @Override
    public DriveStrategy driveCar(Car carInFront) {
        driveOnTransition();
        checkIfEnded();
        return ended ? DriveStrategyDecider.getBestStrategy(myCar, carInFront) : this;
    }

    private void driveOnTransition() {
        myCar.accelerate(transition.getDirection());
    }

    private void checkIfEnded() {
        Point2D transitEndPoint = transition.getEndPoint2D();
        double distToEnd = transitEndPoint.distance(myCar.getPosition());
        if (distToEnd <= 5) endTransition();
    }

    private void endTransition() {
        myCar.setPosition(transition.getEndPoint2D());
        myCar.setVelocity(myCar.getCurrentRoad().getDirection().multiply(myCar.getSpeed()));
        ended = true;
    }

}