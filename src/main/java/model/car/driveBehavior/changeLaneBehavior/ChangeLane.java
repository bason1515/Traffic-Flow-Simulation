package model.car.driveBehavior.changeLaneBehavior;

import model.road.Road;

public interface ChangeLane {
    boolean checkIfEnded();

    boolean shouldChangeToRight();

    void initTransition(Road road);

    boolean shouldOvertake();
}
