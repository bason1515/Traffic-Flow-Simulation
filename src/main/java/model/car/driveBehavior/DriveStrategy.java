package model.car.driveBehavior;

import model.car.Car;

public interface DriveStrategy {
    DriveStrategy driveCar(Car carInFront);
}
