package model.car;

import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LimitedMovingPoint extends MovingPoint {
    Limitation limits;
    private double acceleration;

    public LimitedMovingPoint(Point2D startingPoint, Limitation limits, Point2D direction) {
        super(startingPoint, direction);
        this.limits = limits;
    }

    @Override
    public void applyVelocityToPosition(double elapsedSeconds) {
        setVelocity(acceleration * 5.5 * elapsedSeconds + getSpeed());
        setVelocity(Math.min(limits.getMaxVel(), getVelocity()));
        setVelocity(Math.max(0.0, getVelocity()));
        super.applyVelocityToPosition(elapsedSeconds);
    }

    public void accelerate() {
        acceleration = limits.getMaxAccel();
        double proc = getSpeed() / limits.getMaxVel();
        double speedFactor = 2 - proc * 1.5;
        System.out.println(speedFactor);
        acceleration = limits.getMaxAccel() * speedFactor;
//        Point2D newVelocity = getVelocity().add(limits.limitWithMaxAccel(vector));
//        if (newVelocity.magnitude() > limits.getMaxVel())
//            setVelocity(limits.limitWithMaxVelo(newVelocity));
//        else setVelocity(newVelocity);
    }

    public void slowDown() {
        acceleration = limits.getMaxBreak();
//        Point2D slowDownForce = Limitation.limit(getVelocity(), limits.getMaxBreak() * scalar);
//        if (getVelocity().magnitude() < slowDownForce.magnitude()) stopCar();
//        else setVelocity(getVelocity().add(slowDownForce));
    }

    private void stopCar() {
        setVelocity(0.0);
    }

}
