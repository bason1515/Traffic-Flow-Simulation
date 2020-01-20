package model.car.driveBehavior;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import model.car.Car;
import model.road.Road;

import java.util.concurrent.ThreadLocalRandom;

public class DriveOnRoad implements DriveStrategy {
    private static ThreadLocalRandom rng = ThreadLocalRandom.current();
    private Road drivenRoad;
    private Car myCar;
    private Car carInFront;

    private double deltaX;
    private double deltaV;

    private double rnd4 = rng.nextGaussian();
    private double nrnd = rng.nextGaussian();
    private double ax;
    private double abx;
    private double sdx;
    private double sdv;
    private double cldv;
    private double opdv;

    public DriveOnRoad(Car myCar, Car carInFront) {
        this.myCar = myCar;
        this.carInFront = carInFront;
        this.drivenRoad = myCar.getCurrentRoad();
    }

    @Override
    public DriveStrategy driveCar(Car carInFront) {
        drive();
        return DriveStrategyDecider.getBestStrategy(myCar, carInFront);
    }

    private void drive() {
        if (carInFront != null) {
            calculateParameters();
            // Closing in
            if (deltaV > sdv) {
                decelerate();
                myCar.getView().setFill(Color.YELLOW);
            }
            // Emergency
            else if (deltaX < abx) {
                decelerate();
                myCar.getView().setFill(Color.RED);
            }
            // Following
            else if (deltaV < opdv || deltaX > sdx) {
                freeDrive(drivenRoad);
                myCar.getView().setFill(Color.GREEN);
            } else {
                myCar.getView().setFill(Color.BLUE);
            }
        } else {
            myCar.getView().setFill(Color.BLACK);
            freeDrive(drivenRoad);
        }
    }

    public void calculateParameters() {
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

    public void wiedemannModel() {
        double v = Math.min(myCar.getSpeed(), carInFront.getSpeed());

        ax = 1;

        double bxAdd = 1;
        double bx = bxAdd * Math.sqrt(v);
        abx = ax + bx;

        double ex = 2;
        sdx = ax + ex * bx;

        double cx = 3;
        sdv = Math.pow((deltaX - ax) / cx, 2);

        cldv = sdv;

        opdv = cldv * -1.2;
//        System.out.println(ax + " |abx| " + abx + " |sdx| " + sdx + " |sdv| " + sdv + " |cldv| " + cldv + " |opdv| " + opdv + "|| dx: " + deltaX + " dv: " + deltaV);
    }

    private void decelerate() {
        double b = Math.abs(0.3 * (rnd4 + nrnd));
        System.out.println(myCar.getCarId() + ": " + b);
        myCar.slowDown(b);
    }

    private void freeDrive(Road target) {
        myCar.accelerate(target.getDirection());
    }

}
