package model.vehicle.changeLaneBehavior;

import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;
import model.road.Road;
import model.vehicle.Vehicle;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class GapAcceptanceModel {
    @Getter
    @Setter
    private static double minLeadGap, minLagGap;
    @Getter
    private Vehicle me, lead, lag;
    private double leadGap, lagGap;
    private boolean leadAccept, lagAccept;

    public GapAcceptanceModel(Vehicle myCar) {
        this.me = myCar;
        minLagGap = 50;
        minLeadGap = 25;
    }

    public boolean isLeftLaneAccepted() {
        leadAccept = false;
        lagAccept = false;
        me.getCurrentRoad().getLeft().ifPresent(this::acceptance);
        return leadAccept && lagAccept;
    }

    public boolean isRightLaneAccepted() {
        leadAccept = false;
        lagAccept = false;
        me.getCurrentRoad().getRight().ifPresent(this::acceptance);
        return leadAccept && lagAccept;
    }

    private void acceptance(Road target) {
        findLeadLag(target);
        gapAcceptance();
    }

    private void findLeadLag(Road target) {
        lag = findLaggingCar(target)
                .orElse(null);
        lead = Optional.ofNullable(lag).map(Vehicle::getCarInFront)
                .orElseGet(() -> findLeadingCar(target))
                .orElse(null);
    }

    private void gapAcceptance() {
        leadAccept = Optional.ofNullable(lead)
                .map(this::isAboveMinLeadGap)
                .orElse(true);
        lagAccept = Optional.ofNullable(lag)
                .map(this::isAboveMinLagGap)
                .orElse(true);
    }

    public double avgRoadSpeed() {
        double avg = 0.0;
        int count = (int) Stream.of(lag, lead).filter(Objects::nonNull).count();
        avg += Optional.ofNullable(lag).map(Vehicle::getSpeed).orElse(0.0);
        avg += Optional.ofNullable(lead).map(Vehicle::getSpeed).orElse(0.0);
        return count == 0 ? 1000.0 : avg / count;
    }

    private boolean isAboveMinLeadGap(Vehicle car) {
        double gap = calculateGap(car);
        double gapMod = me.getSpeed() - car.getSpeed();
        gapMod = Math.max(0, gapMod);
        return gap >= myAndHisBumper(car) + 20 + gapMod;
    }

    private boolean isAboveMinLagGap(Vehicle car) {
        double gap = calculateGap(car);
        double minGap = (car.getSpeed() - me.getSpeed()) + 20;
        minGap = Math.max(20, minGap);
        return gap >= myAndHisBumper(car) + minGap;
    }

    private double calculateGap(Vehicle target) {
        return target.getPosition().distance(me.getPosition()) - myAndHisBumper(target);
    }

    private void calculateGaps() {
        lagGap = Optional.ofNullable(lag)
                .map(lag -> lag.getPosition().distance(me.getPosition()))
                .orElse(0.0);
        leadGap = Optional.ofNullable(lead)
                .map(lead -> lead.getPosition().distance(me.getPosition()))
                .orElse(0.0);
    }

    private Optional<Vehicle> findLeadingCar(Road target) {
        Point2D myPosition = me.getPosition();
        Point2D myDirection = me.getDirection();
        return target.getOnRoad().stream()
                .filter(car -> car.getPosition().subtract(myPosition).angle(myDirection) <= 90.0)
                .min((o1, o2) -> (int) (o1.getPosition().distance(myPosition) - o2.getPosition().distance(myPosition)));
    }

    private Optional<Vehicle> findLaggingCar(Road target) {
        Point2D myPosition = me.getPosition();
        Point2D myDirection = me.getDirection();
        return target.getOnRoad().stream()
                .filter(car -> car.getPosition().subtract(myPosition).angle(myDirection) > 90.0)
                //.filter(car -> myPosition.distance(car.getPosition()) < car.getSpeed() * 2)
                .min((o1, o2) -> (int) (o1.getPosition().distance(myPosition) - o2.getPosition().distance(myPosition)));
    }

    public double myAndHisBumper(Vehicle car) {
        return me.getHeight() / 2 + car.getHeight() / 2;
    }

    public Optional<Vehicle> getLead() {
        return Optional.ofNullable(lead);
    }

    public Optional<Vehicle> getLag() {
        return Optional.ofNullable(lag);
    }
}
