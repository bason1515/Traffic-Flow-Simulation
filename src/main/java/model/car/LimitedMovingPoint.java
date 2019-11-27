package model.car;

import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LimitedMovingPoint extends MovingPoint {
    Limitation limits;

    public LimitedMovingPoint(Point2D startingPoint, Limitation limits) {
        super(startingPoint);
        this.limits = limits;
    }

    public void accelerate(Point2D vector) {
        Point2D newVelocity = getVelocity().add(limits.limitWithMaxAccel(vector));
        if (newVelocity.magnitude() > limits.getMaxVel())
            setVelocity(limits.limitWithMaxVelo(newVelocity));
        else setVelocity(newVelocity);
    }

    public void slowDown(double scalar) {
        Point2D slowDownForce = Limitation.limit(getVelocity(), limits.getMaxBreak() * scalar);
        if (getVelocity().magnitude() < slowDownForce.magnitude()) stopCar();
        else setVelocity(getVelocity().add(slowDownForce));
    }

    private void stopCar() {
        setVelocity(Point2D.ZERO);
    }

}
