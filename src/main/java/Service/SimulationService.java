package Service;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import lombok.Data;
import model.car.Car;
import model.road.Road;
import repository.CarRepository;
import repository.RoadRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Data
public class SimulationService {
    private RoadRepository roadRepo;
    private CarRepository carRepo;

    public SimulationService(RoadRepository roadRepository, CarRepository carRepository) {
        roadRepo = roadRepository;
        carRepo = carRepository;
    }

    public void addCar(Car car) {
        carRepo.save(car);
        car.getCurrentRoad().addOnRoad(car);
    }

    public List<Rectangle> getCarsView() {
        return carRepo.getAll().stream().map(Car::getView).collect(Collectors.toList());
    }

    public void addRoad(Road road) {
        roadRepo.save(road);
    }

    public void addLine(Long id) {
        Road r = roadRepo.byId(id);
        if (r.getLeft() != null) {
            addLine(r.getLeft().getRoadId());
        } else {
            Point2D offset = new Point2D(r.getDirection().getY(), r.getDirection().getX() * -1);
            offset = offset.multiply(Road.LINE_OFFSET);
            Road newRoad = new Road(r.getStartX() + offset.getX(), r.getStartY() + offset.getY(),
                    r.getEndX() + offset.getX(), r.getEndY() + offset.getY());
            newRoad.setRight(r);
            r.setLeft(newRoad);
            roadRepo.save(newRoad);
        }
    }

    public void updateCars() {
        carRepo.getAll().forEach(this::driveCar);
    }

    public void updateSim(double elapsedTime) {
        double elapsedSeconds = elapsedTime / 1_000_000_000.0;
        carRepo.getAll().forEach(c -> {
            Point2D distTraveled = c.getVelocity().multiply(elapsedSeconds);
            c.setPosition(c.getPosition().add(distTraveled));
        });
    }

    private void driveCar(Car car) {
        final Point2D position = car.getPosition();
        Point2D velocity = car.getVelocity();
        final Road road = car.getCurrentRoad();
        if (car.getCurrentRoad().getEndPoint2D().distance(position) < 5) {
            if (car.isTransition()) {
                car.setPosition(car.getCurrentRoad().getEndPoint2D());
                car.setCurrentRoad(road.getLeft());
                car.getCurrentRoad().addOnRoad(car);
                car.setVelocity(car.getCurrentRoad().getDirection().multiply(car.getVelocity().magnitude()));
                car.setTransition(false);
                return;
            }
            car.setX(road.getStartX());
            car.setY(road.getStartY());
            return;
        }
        if (isCarInFront(car, 40) && !car.isTransition()) {
            if (changeLine(car)) return;
        }
        if (isCarInFront(car, 25) && !car.isTransition()) {
            stopCar(car);
            return;
        }
        Point2D force = road.getDriveDirection(position);
        Point2D accel = Point2D.ZERO;

        accel = accel.add(force);
        accel = accel.normalize().multiply(car.getMaxAccel());

        velocity = velocity.add(accel);
        if (velocity.magnitude() >= car.getMaxVel())
            velocity = velocity.normalize().multiply(car.getMaxVel());

        car.setVelocity(velocity);

    }

    private boolean isCarInFront(Car car, double dist) {
        Point2D driveVec = car.getVelocity();
        Optional<Car> result = carRepo.getAll().stream()
                .filter(target -> {
                    if (target.getCarId().equals(car.getCarId())) return false;
                    Point2D vecToTarget = new Point2D(target.getX() - car.getX(), target.getY() - car.getY());
                    double angle = driveVec.angle(vecToTarget);
                    return angle < 15 && vecToTarget.magnitude() < dist;
                })
                .findAny();
        return result.isPresent();
    }

    private void stopCar(Car car) {
        Point2D velVec = car.getVelocity();
        velVec.normalize();
        velVec = velVec.multiply(car.getMaxBreak());
        if (velVec.magnitude() > car.getSpeed()) car.setVelocity(Point2D.ZERO);
        else car.setVelocity(car.getVelocity().add(velVec));
    }

    private boolean changeLine(Car car) {
        Road myRoad = car.getCurrentRoad();
        Road target;
        if (myRoad.getLeft() == null) {
            if (myRoad.getRight() == null)
                return false;
            target = myRoad.getRight();
        } else target = myRoad.getLeft();

        if (myRoad.getLeft() != null && myRoad.getRight() != null) {
            ThreadLocalRandom rng = ThreadLocalRandom.current();
            if (rng.nextDouble() < 0.5) target = myRoad.getLeft();
            else target = myRoad.getRight();
        }

        Point2D pos = car.getPosition();
        double distance = car.getCurrentRoad().getStartPoint2D().subtract(pos).magnitude();
        Point2D desPos = target.getPointOnLine(distance + 20);

        for (Car c : target.getOnRoad()) {
            if (c.getPosition().distance(car.getPosition()) < 50) {
                return false;
            }
        }

        Line dirLine = new Line(car.getX(), car.getY(), desPos.getX(), desPos.getY());
        Road road = new Road(pos.getX(), pos.getY(), desPos.getX(), desPos.getY());
        road.setLeft(target);
        car.getCurrentRoad().removeOnRoad(car);
        car.setCurrentRoad(road);
        car.setVelocity(road.getDirection().multiply(car.getVelocity().magnitude()));
        car.setTransition(true);
        return true;
    }

    public Point2D turnLeft(Point2D vec, double angle) {
        double x2 = Math.cos(angle) * vec.getX() - Math.sin(angle) * vec.getY();
        double y2 = Math.sin(angle) * vec.getX() + Math.cos(angle) * vec.getY();
        return new Point2D(x2, y2);
    }
}
