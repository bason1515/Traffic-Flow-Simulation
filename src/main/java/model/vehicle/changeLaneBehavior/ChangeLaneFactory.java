package model.vehicle.changeLaneBehavior;

import model.road.RoadType;
import model.vehicle.Vehicle;

public class ChangeLaneFactory {

    public static ChangeLane createChangeLane(Vehicle car) {
        RoadType roadType = car.getCurrentRoad().getType();
        if (roadType.equals(RoadType.LANE))
            return new ChangeLaneForLane(car);
        else
            return new ChangeLaneForRamp(car);
    }

}
