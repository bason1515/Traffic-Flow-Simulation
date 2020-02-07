package model.car.driveBehavior;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import model.car.Car;
import model.road.Road;

import java.util.concurrent.ThreadLocalRandom;

public class DriveOnRoad {
    private static ThreadLocalRandom rng = ThreadLocalRandom.current();
    @Setter
    private Road drivenRoad;
    private Car myCar;
    private Car carInFront;
    @Getter
    private CarStatus status;

    private double deltaX;
    private double deltaV;

    private static final double nrnd = rng.nextDouble();
    private double l;
    private double ax;
    private double abx;
    private double sdx;
    private double sdv;
    private double cldv;
    private double opdv;

    public DriveOnRoad(Car myCar) {
        this.myCar = myCar;
        this.drivenRoad = myCar.getCurrentRoad();
        status = CarStatus.FREE;
    }

    public void drive(Car carInFront) {
        this.carInFront = carInFront;
        performAction();
    }

    private void performAction() {
        if (carInFront != null) {
            calculateParameters();
            if (deltaX < abx) {
                status = CarStatus.BREAK;
                decelerate(0.7);
                myCar.getView().setFill(Color.RED);
                if (deltaX <= 0.5) {
                    status = CarStatus.COLLISION;
                    myCar.setVelocity(0.0);
                }
            } else if (deltaV > sdv) {
                status = CarStatus.CLOSING_IN;
                if (deltaV > cldv)
                    decelerate(0.3);
                myCar.getView().setFill(Color.YELLOW);
            } else if (deltaV < opdv || deltaX > sdx) {
                status = CarStatus.FREE;
                freeDrive();
                myCar.getView().setFill(Color.GREEN);
            } else {
                status = CarStatus.FOLLOW;
                myCar.getView().setFill(Color.BLUE);
            }
        } else {
            status = CarStatus.FREE;
            myCar.getView().setFill(Color.BLACK);
            freeDrive();
        }
    }

    private void calculateParameters() {
        deltaV = myCar.getSpeed() - carInFront.getSpeed();
        deltaX = calcDeltaX();
        wiedemannModel();
    }

    private double calcDeltaX() {
        Point2D carPos = myCar.getPosition();
        Point2D carInFrontPos = carInFront.getPosition();
        double distanceBetween = carPos.distance(carInFrontPos);
        double carsLength = (myCar.getHeight() / 2) + (carInFront.getHeight() / 2);
        return distanceBetween - carsLength;
    }

    private void wiedemannModel() {
        l = carInFront.getHeight() / 2;
        double v = Math.min(myCar.getSpeed(), carInFront.getSpeed());

        ax = 1;

        double bxAdd = 1.5 + myCar.getRnd()[0];
        double bx = bxAdd * Math.sqrt(v);
        abx = ax + bx;

        double ex = 2 + myCar.getRnd()[1];
        sdx = ax + ex * bx;

        double cx = 18 + myCar.getRnd()[2];
        sdv = Math.pow((deltaX - ax) / cx, 2);

        cldv = sdv * 1.1;

        opdv = cldv * -1.2;
    }

    private void decelerate(double scale) {
        myCar.setDirection(drivenRoad.getDirection());
        double b = Math.abs(scale * (0.5 + nrnd));
        myCar.slowDown();
    }

    private void freeDrive() {
        myCar.setDirection(drivenRoad.getDirection());
        myCar.accelerate();
    }

    @Override
    public String toString() {
        return ax + " |abx| " + abx + " |sdx| " + sdx + " |sdv| " + sdv + " |cldv| " +
                cldv + " |opdv| " + opdv + "|| dx: " + deltaX + " dv: " + deltaV;
    }

}
