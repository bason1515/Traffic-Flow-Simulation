package Service;

import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;
import model.road.Road;
import model.vehicle.Vehicle;
import repository.RoadRepository;
import repository.VehicleRepository;

import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class VehicleService {

    private VehicleRepository carRepo;
    private RoadRepository roadRepo;
    private double elapsedSeconds;

    public VehicleService(VehicleRepository vehicleRepository, RoadRepository roadRepository) {
        this.carRepo = vehicleRepository;
        this.roadRepo = roadRepository;
    }

    public void addCar(Vehicle car) {
        car.getCurrentRoad().addOnRoad(car);
        carRepo.save(car);
    }

    public List<Rectangle> getAllCarsView() {
        return carRepo.getAll().stream().map(Vehicle::getView).collect(Collectors.toList());
    }

    public void updateCars(double elapsedTime) {
        elapsedSeconds = elapsedTime / 1_000_000_000.0;
        Stream<Road> roadStream = roadRepo.getAll().stream();
        roadStream.forEach(this::driveCarsOnRoad);
        carRepo.getAll().forEach(c -> c.applyVelocityToPosition(elapsedSeconds));
        applyRoadBorder();
    }

    private void driveCarsOnRoad(Road road) {
        updateCarsInFront(road);
        for (int i = 0; i < road.getOnRoad().size(); i++) {
            Vehicle currentCar = road.getOnRoad().get(i);
            if(currentCar.getCurrentRoad().equals(road)) // Avoid double updating car while changing lane
                currentCar.performDrive(elapsedSeconds);
        }
    }

    private void updateCarsInFront(Road road) {
        ListIterator<Vehicle> carIterable = road.getOnRoad().listIterator();
        if (!carIterable.hasNext()) return;
        Vehicle currentCar = carIterable.next();
        while (carIterable.hasNext()) {
            Vehicle carInFront = carIterable.next();
            currentCar.setCarInFront(carInFront);
            currentCar = carInFront;
        }
        Vehicle carOnNextRoad = currentCar.getCurrentRoad().getNext()
                .map(road1 -> road1.getOnRoad().peekFirst())
                .orElse(null);
        currentCar.setCarInFront(carOnNextRoad);
    }

    private void applyRoadBorder() {
        for (Road road : roadRepo.getAll()) {
            if (!road.getOnRoad().isEmpty()) {
                Vehicle car = road.getOnRoad().getLast();
                double distToRoadStart = road.getStartPoint2D().distance(car.getPosition());
                if (distToRoadStart >= road.getLength()) {
                    road.getNext().ifPresent(next -> next.moveCarToThisRoad(car));
                    if (!road.getNext().isPresent())
                        deleteCar(car);
                }
            }
        }
    }

    private void deleteCar(Vehicle car) {
        if (!car.getDriver().getChangeLane().checkIfEnded())
            car.getDriver().getChangeLane().endTransition();
        car.getCurrentRoad().removeOnRoad(car);
        carRepo.remove(car.getCarId());
    }

    public void restart() {
        carRepo.removeAll();
    }

}
