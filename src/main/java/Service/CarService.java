package Service;

import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;
import model.car.Car;
import model.car.driveBehavior.DriveOnRoad;
import model.road.Road;
import repository.CarRepository;
import repository.RoadRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class CarService {

    private CarRepository carRepo;
    private RoadRepository roadRepo;
    private List<Car> carBuffer;

    public CarService(CarRepository carRepository, RoadRepository roadRepository) {
        this.carRepo = carRepository;
        this.roadRepo = roadRepository;
        this.carBuffer = new ArrayList<>();
    }

    public void addCar(Car car) {
        car.getCurrentRoad().addOnRoad(car);
        carRepo.save(car);
    }

    public List<Rectangle> getAllCarsView() {
        return carRepo.getAll().stream().map(Car::getView).collect(Collectors.toList());
    }

    public void updateCars(double elapsedTime) {
        double elapsedSeconds = elapsedTime / 1_000_000_000.0;
        Stream<Road> roadStream = roadRepo.getAll().stream();
        roadStream.forEach(this::driveCarsOnRoad);
        carRepo.getAll().forEach(c -> c.applyVelocityToPosition(elapsedSeconds));
        applyRoadBorder();
        putFromBuffer();
    }

    private void driveCarsOnRoad(Road road) {
        LinkedList<Car> onRoadClone = (LinkedList<Car>) road.getOnRoad().clone();
        ListIterator<Car> carIterable = onRoadClone.listIterator();
        if (!carIterable.hasNext()) return;
        Car currentCar = carIterable.next();
        while (carIterable.hasNext()) {
            Car carInFront = carIterable.next();
            currentCar.performDrive(carInFront);
            currentCar = carInFront;
        }
        currentCar.performDrive(null);
    }

    private void applyRoadBorder() {
        for (Road road : roadRepo.getAll()) {
            if (!road.getOnRoad().isEmpty()) {
                Car car = road.getOnRoad().getLast();
                double distToBorder = road.getEndPoint2D().distance(car.getPosition());
                if (distToBorder < Road.LINE_OFFSET) { // end of road
                    road.removeOnRoad(car);
                    putOnBuffer(car);
                }
            }
        }
    }

    private void putOnBuffer(Car car) {
        carRepo.remove(car.getCarId());
        carBuffer.add(car);
        car.getView().setVisible(false);
        car.setVelocity(Point2D.ZERO);
        car.setPosition(Point2D.ZERO);
        car.setCurrentRoad(null);
    }

    private void putFromBuffer() {
        Iterator<Road> freeRoads = roadRepo.getAll().stream().filter(this::emptyStartOfRoad).iterator();
        while (!carBuffer.isEmpty() && freeRoads.hasNext()) {
            Road road = freeRoads.next();
            Car car = carBuffer.remove(0);
            initCarOnRoad(car, road);
        }
    }

    private boolean emptyStartOfRoad(Road road) {
        if (road.getOnRoad().isEmpty()) return true;
        Car closestCar = road.getOnRoad().getFirst();
        double distance = road.getStartPoint2D().distance(closestCar.getPosition());
        return distance > 50;
    }

    private void initCarOnRoad(Car car, Road road) {
        carRepo.save(car);
        road.addOnRoad(car);
        car.getView().setVisible(true);
        car.setVelocity(road.getDirection().multiply(car.getLimits().getMaxAccel()));
        car.setPosition(road.getStartPoint2D());
        car.setCurrentRoad(road);
        car.setDriver(new DriveOnRoad(car, null));
    }

}
