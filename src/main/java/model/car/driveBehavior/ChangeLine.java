package model.car.driveBehavior;

import javafx.geometry.Point2D;
import lombok.Getter;
import model.car.Car;
import model.road.Road;

import java.util.Optional;

public class ChangeLine {
    private Road target;
    @Getter
    private Road transition;
    private Car myCar;
    private boolean ended = true;

    public ChangeLine(Car myCar) {
        this.myCar = myCar;
    }

    public boolean shouldOvertake() {
        if(myCar.getDriver().getDriveOnRoad().getStatus() == CarStatus.FREE) return false;
        Road leftRoad = myCar.getCurrentRoad().getLeft();
        Optional<Car> carInFront = Optional.ofNullable(myCar.getDriver().getCarInFront());
        boolean canGoFaster = carInFront.filter(c -> myCar.getLimits().getMaxVel() > c.getSpeed()).isPresent();
        return canChangeLine(leftRoad) && canGoFaster;
    }

    public boolean shouldChangeToRight() {
        Road rightRoad = myCar.getCurrentRoad().getRight();
        return canChangeLine(rightRoad);
    }

    private boolean canChangeLine(Road target) {
        if (target == null) return false;
        boolean isThereACar = target.getOnRoad().stream()
                .anyMatch(c -> c.getPosition().distance(myCar.getPosition()) < 50);
        return !isThereACar;
    }

    public void initTransition(Road target) {
        this.target = target;
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
        myCar.getDriver().getDriveOnRoad().setDrivenRoad(transition);
        ended = false;
    }

    public boolean checkIfEnded() {
        if(ended) return true;
        Point2D transitStartPoint = transition.getStartPoint2D();
        double distToStart = transitStartPoint.distance(myCar.getPosition());
        if (distToStart >= transition.getLenght()) endTransition();
        return ended;
    }

    private void endTransition() {
        myCar.setPosition(transition.getEndPoint2D());
        myCar.setVelocity(myCar.getCurrentRoad().getDirection().multiply(myCar.getSpeed()));
        myCar.getDriver().getDriveOnRoad().setDrivenRoad(myCar.getCurrentRoad());
        ended = true;
    }

}
