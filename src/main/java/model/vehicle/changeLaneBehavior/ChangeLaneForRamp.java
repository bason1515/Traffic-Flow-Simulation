package model.vehicle.changeLaneBehavior;

import model.vehicle.Vehicle;

public class ChangeLaneForRamp extends ChangeLaneForLane implements ChangeLane {

    public ChangeLaneForRamp(Vehicle car) {
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
