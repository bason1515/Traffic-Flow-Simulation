package model.vehicle.changeLaneBehavior;

import model.road.Road;

public interface ChangeLane {
    boolean checkIfEnded();

    void endTransition();

    boolean shouldChangeToRight();

    void initTransition(Road road);

    boolean shouldChangeToLeft();
}
