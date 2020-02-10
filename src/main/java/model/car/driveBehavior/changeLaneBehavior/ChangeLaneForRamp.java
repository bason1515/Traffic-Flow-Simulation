package model.car.driveBehavior.changeLaneBehavior;

import model.car.Car;

public class ChangeLaneForRamp extends ChangeLaneForLane implements ChangeLane {

    public ChangeLaneForRamp(Car car) {
        super(car);
    }

    @Override
    public boolean shouldOvertake() {
        boolean gap = getGapModel().isLeftLineAccepted();
        double distanceToLead = getGapModel().getLag()
                .map(lead -> lead.getPosition().distance(getMyCar().getPosition()))
                .orElse(100.0);
        boolean justBehindLead = distanceToLead < 20 && distanceToLead > 15;
        return justBehindLead || gap;
    }

}
