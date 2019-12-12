package model.roadObject;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Getter;
import lombok.Setter;
import model.car.Car;
import model.car.Limitation;
import model.road.Road;
import repository.CarRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class VehicleSpawner {
    @Getter
    @Setter
    private List<Road> roads;
    @Getter
    @Setter
    private Limitation carLimits, truckLimits;
    private CarRepository carRepo;
    private IntegerProperty vehiclePerHour;
    private ReadOnlyDoubleWrapper spawnRate;
    private double lastSpawnTimeInSec;

    public VehicleSpawner(CarRepository carRepository) {
        this.carRepo = carRepository;
        roads = new ArrayList<>();
        carLimits = new Limitation(1.5, -1.5, 80);
        truckLimits = new Limitation(1, -1, 60);
        vehiclePerHour = new SimpleIntegerProperty(this, "vehiclePerHour", 0);
        spawnRate = new ReadOnlyDoubleWrapper(this, "spawnRate");
        lastSpawnTimeInSec = 0;
        bindSpawnRate();
    }

    private void bindSpawnRate() {
        spawnRate.bind(Bindings.createDoubleBinding(() -> {
            if (this.getVehiclePerHour() == 0) return 0.0;
            return 3600.0 / this.getVehiclePerHour();
        }, vehiclePerHour));
    }

    public void update(double elapsedSeconds) {
        if (spawnRate.get() == 0.0) return;
        lastSpawnTimeInSec += elapsedSeconds;
        while (lastSpawnTimeInSec >= spawnRate.get()) {
            lastSpawnTimeInSec -= spawnRate.get();
            spawnVehicle();
        }
    }

    private void spawnVehicle() {
        Optional<Road> freeRoad = findFreeRoad();
        freeRoad.ifPresent(this::createVehicleOnRoad);
    }

    private Optional<Road> findFreeRoad() {
        return roads.stream().filter(this::isRoadFree).findAny();
    }

    private boolean isRoadFree(Road road) {
        if (road.getOnRoad().isEmpty()) return true;
        Car closestCar = road.getOnRoad().getFirst();
        double distance = road.getStartPoint2D().distance(closestCar.getPosition());
        return distance > 10;
    }

    private void createVehicleOnRoad(Road road) {
        Car car = new Car(road.getStartPoint2D(), carLimits, 5, 8, road);
        carRepo.save(car);
        road.addOnRoad(car);
    }

    public void addRoads(Road... roads) {
        this.roads.addAll(Arrays.asList(roads));
    }

    public void removeRoads(Road... roads) {
        this.roads.removeAll(Arrays.asList(roads));
    }

    public int getVehiclePerHour() {
        return vehiclePerHour.get();
    }

    public IntegerProperty vehiclePerHourProperty() {
        return vehiclePerHour;
    }

    public void setVehiclePerHour(int vehiclePerHour) {
        this.vehiclePerHour.set(vehiclePerHour);
    }

}
