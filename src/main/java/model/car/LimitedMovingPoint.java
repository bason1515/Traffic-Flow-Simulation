package model.car;

import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LimitedMovingPoint extends MovingPoint {
    Limitation limits;
    private double acceleration;
    private double desAcceleration;

    public LimitedMovingPoint(Point2D startingPoint, Limitation limits, Point2D direction) {
        super(startingPoint, direction);
        this.limits = limits;
    }

    @Override
    public void applyVelocityToPosition(double elapsedSeconds) {
        incCurrentAcceleration(elapsedSeconds);
        setVelocity(acceleration * 3.6 * elapsedSeconds + getSpeed());
        setVelocity(Math.min(limits.getMaxVel(), getVelocity()));
        setVelocity(Math.max(0.0, getVelocity()));
        super.applyVelocityToPosition(elapsedSeconds);
    }

    private void incCurrentAcceleration(double elapsedSeconds) {
        if (desAcceleration <= 0) return;
        acceleration += elapsedSeconds / 3;
        acceleration = Math.min(desAcceleration, acceleration);
    }

    public void accelerate() {
        if (acceleration < 0) acceleration = 0;
        desAcceleration = limits.getMaxAccel();
        double proc = getSpeed() / limits.getMaxVel();
        double speedFactor = 2 - proc * 1.5;
        desAcceleration = limits.getMaxAccel() * speedFactor;
    }

    public void slowDown(double brakeForce) {
        acceleration = brakeForce;
    }

    private void stopCar() {
        setVelocity(0.0);
    }

}
