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
            c.setPosition(applyRoadBorder(c, distTraveled));
        });
    }

    private Point2D applyRoadBorder(Car car, Point2D distTraveled) {
        Road road = car.getCurrentRoad();
        double distToBorder = road.getEndPoint2D().distance(car.getPosition());
        if (distToBorder < Road.LINE_OFFSET) { // end of road
            car.setTransition(Optional.empty()); // canceling transit
            car.setVelocity(car.getCurrentRoad().getDirection().multiply(car.getSpeed()));
            return road.getStartPoint2D();
        }
        if (car.getTransition().isPresent()) {
            Point2D transitEndPoint = car.getTransition().get().getEndPoint2D();
            distToBorder = transitEndPoint.distance(car.getPosition());
            if (distTraveled.magnitude() >= distToBorder) return handleTransit(car); // end of transit
        }
        return car.getPosition().add(distTraveled);
    }

    private Point2D handleTransit(Car car) {
        Point2D endPoint = car.getTransition().get().getEndPoint2D();
        car.setTransition(Optional.empty());
        car.setVelocity(car.getCurrentRoad().getDirection().multiply(car.getSpeed()));
        return endPoint;
    }

    private void driveCar(Car car) {
        final Point2D position = car.getPosition();
        Point2D velocity = car.getVelocity();
        Road road = car.getCurrentRoad();
        if (car.getTransition().isPresent()) {
            road = car.getTransition().get();
        }

        if (!car.getTransition().isPresent()) {
            if (isCarInFront(car, 40)) {
                if (road.getLeft() != null && changeLine(car, road.getLeft())) return;
            }
            if (road.getRight() != null && changeLine(car, road.getRight())) return;

            if (isCarInFront(car, 25)) {
                stopCar(car);
                return;
            }
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

    private boolean changeLine(Car car, Road target) {
        Point2D pos = car.getPosition();
        double distance = car.getCurrentRoad().getStartPoint2D().subtract(pos).magnitude();
        Point2D desPos = target.getPointOnLine(distance + car.getSpeed());

        boolean isThereACar = target.getOnRoad().stream()
                .anyMatch(c -> c.getPosition().distance(car.getPosition()) < 50);
        if (isThereACar) return false;

        Line dirLine = new Line(car.getX(), car.getY(), desPos.getX(), desPos.getY());
        Road road = new Road(pos.getX(), pos.getY(), desPos.getX(), desPos.getY());
        car.getCurrentRoad().removeOnRoad(car);
        target.addOnRoad(car);
        car.setCurrentRoad(target);
        car.setVelocity(road.getDirection().multiply(car.getSpeed()));
        car.setTransition(Optional.of(road));
        return true;
    }

//    public Point2D turnLeft(Point2D vec, double angle) {
//        double x2 = Math.cos(angle) * vec.getX() - Math.sin(angle) * vec.getY();
//        double y2 = Math.sin(angle) * vec.getX() + Math.cos(angle) * vec.getY();
//        return new Point2D(x2, y2);
//    }
}
