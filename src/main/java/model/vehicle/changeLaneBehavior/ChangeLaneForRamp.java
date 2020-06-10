package model.vehicle.changeLaneBehavior;

import model.vehicle.Vehicle;

public class ChangeLaneForRamp extends ChangeLaneForLane implements ChangeLane {

    public ChangeLaneForRamp(Vehicle car) {
        super(car);
    }

    @Override
    public boolean shouldChangeToLeft() {
        boolean gap = getGapModel().isLeftLaneAccepted();
        double distanceToLead = getGapModel().getLead()
                .map(lead -> lead.getPosition().distance(getMyCar().getPosition()) -
                        (getMyCar().getHeight() / 2 + lead.getHeight() / 2))
                .orElse(100.0);
        boolean justBehindLead = distanceToLead < 15 && distanceToLead > 10;
        return justBehindLead || gap;
    }

}
