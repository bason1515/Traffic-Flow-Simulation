package model.roadObject;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
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
import java.util.concurrent.ThreadLocalRandom;

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
    private DoubleProperty truckChance;
    private double lastSpawnTimeInSec;

    public VehicleSpawner(CarRepository carRepository) {
        this.carRepo = carRepository;
        roads = new ArrayList<>();
        carLimits = new Limitation(1.5, -1.5, 80);
        truckLimits = new Limitation(1, -1, 60);
        vehiclePerHour = new SimpleIntegerProperty(this, "vehiclePerHour", 0);
        spawnRate = new ReadOnlyDoubleWrapper(this, "spawnRate");
        truckChance = new SimpleDoubleProperty(this, "truckChance", 0.10);
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
        Car car;
        if (shouldISpawnTruck()) car = new Car(road.getStartPoint2D(), truckLimits, 5, 20, road);
        else car = new Car(road.getStartPoint2D(), carLimits, 5, 8, road);
        carRepo.save(car);
        road.addOnRoad(car);
    }

    private boolean shouldISpawnTruck() {
        return ThreadLocalRandom.current().nextDouble() <= getTruckChance();
    }

    public void addRoad(Road road) {
        this.roads.addAll(road.getAllLanes());
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

    public double getTruckChance() {
        return truckChance.get();
    }

    public DoubleProperty truckChanceProperty() {
        return truckChance;
    }

    public void setTruckChance(double truckChance) {
        this.truckChance.set(truckChance);
    }

}
