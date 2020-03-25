package model.vehicle.changeLaneBehavior;

import model.vehicle.Vehicle;
import model.road.RoadType;

public class ChangeLaneFactory {

    public static ChangeLaneForLane getChangeLane(Vehicle car) {
        RoadType roadType = car.getCurrentRoad().getType();
        if (roadType.equals(RoadType.LANE))
            return new ChangeLaneForLane(car);
        else
            return new ChangeLaneForRamp(car);
    }

}
