package Service;

import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;
import model.car.Car;
import model.road.Road;
import repository.CarRepository;
import repository.RoadRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class CarService {

    private CarRepository carRepo;
    private RoadRepository roadRepo;

    public CarService(CarRepository carRepository, RoadRepository roadRepository) {
        this.carRepo = carRepository;
        this.roadRepo = roadRepository;
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
                double distToRoadStart = road.getStartPoint2D().distance(car.getPosition());
                if (distToRoadStart >= road.getLength()) {
                    deleteCar(car);
                }
            }
        }
    }

    private void deleteCar(Car car) {
        car.getCurrentRoad().removeOnRoad(car);
        carRepo.remove(car.getCarId());
    }

}
