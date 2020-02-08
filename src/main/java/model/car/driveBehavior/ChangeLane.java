package model.car.driveBehavior;

import javafx.geometry.Point2D;
import lombok.Getter;
import model.car.Car;
import model.road.Road;

import java.util.Optional;

public class ChangeLane {
    private Road target;
    @Getter
    private Road transition;
    private Car myCar;
    private boolean ended = true;
    private GapAcceptanceModel gapModel;

    public ChangeLane(Car myCar) {
        this.myCar = myCar;
        gapModel = new GapAcceptanceModel(myCar);
    }

    public boolean shouldOvertake() {
        if (myCar.getDriver().getDriveOnRoad().getStatus() == CarStatus.FREE) return false;
        Optional<Road> leftRoad = myCar.getCurrentRoad().getLeft();
        double myMaxV = myCar.getLimits().getMaxVel();
        boolean canGoFaster = myMaxV - myCar.getSpeed() > 1;
        boolean gap = gapModel.isLeftLineAccepted();
        return gap && canGoFaster;
    }

    public boolean shouldChangeToRight() {
        Optional<Road> rightRoad = myCar.getCurrentRoad().getRight();
        boolean gap = gapModel.isRightLineAccepted();
        double myMaxV = myCar.getLimits().getMaxVel();
        boolean toSlow = gapModel.getLead().map(lead -> myMaxV - lead.getSpeed() >= 3).orElse(false);
        return gap && !toSlow;
    }

    private boolean canChangeLane(Road target) {
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
        Point2D targetPoint = target.getPointOnLane(distance + myCar.getSpeed());
        transition = new Road(position, targetPoint);
    }

    private void changeRoad() {
        myCar.getCurrentRoad().removeOnRoad(myCar);
        target.addOnRoad(myCar);
        myCar.setCurrentRoad(target);
        myCar.setDirection(transition.getDirection());
        myCar.getDriver().getDriveOnRoad().setDrivenRoad(transition);
        ended = false;
    }

    public boolean checkIfEnded() {
        if (ended) return true;
        Point2D transitStartPoint = transition.getStartPoint2D();
        double distToStart = transitStartPoint.distance(myCar.getPosition());
        if (distToStart >= transition.getLength()) endTransition();
        return ended;
    }

    private void endTransition() {
        myCar.setPosition(transition.getEndPoint2D());
        myCar.setDirection(myCar.getCurrentRoad().getDirection());
        myCar.getDriver().getDriveOnRoad().setDrivenRoad(myCar.getCurrentRoad());
        ended = true;
    }

}
