package model.car.driveBehavior.changeLaneBehavior;

import model.car.Car;
import model.road.RoadType;

public class ChangeLaneFactory {

    public static ChangeLaneForLane getChangeLane(Car car) {
        RoadType roadType = car.getCurrentRoad().getType();
        if (roadType.equals(RoadType.LANE))
            return new ChangeLaneForLane(car);
        else
            return new ChangeLaneForRamp(car);
    }

}
