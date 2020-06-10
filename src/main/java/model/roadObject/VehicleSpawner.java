package model.roadObject;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import lombok.Getter;
import lombok.Setter;
import model.road.Road;
import model.vehicle.Limitation;
import model.vehicle.Vehicle;
import model.vehicle.VehicleType;
import repository.VehicleRepository;

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
    private VehicleRepository carRepo;
    private IntegerProperty vehiclePerHour;
    private ReadOnlyDoubleWrapper spawnRate;
    private DoubleProperty truckChance;
    private double lastSpawnTimeInSec;
    @Getter
    @Setter
    private int totalSpawnedVehicles;

    public VehicleSpawner(VehicleRepository vehicleRepository) {
        this.carRepo = vehicleRepository;
        roads = new ArrayList<>();
        carLimits = new Limitation(1.5, -3, 80);
        truckLimits = new Limitation(1, -2, 60);
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
        freeRoad.ifPresent(road -> {
            createVehicleOnRoad(road);
            totalSpawnedVehicles++;
        });
    }

    private Optional<Road> findFreeRoad() {
        return roads.stream().filter(this::isRoadFree).findAny();
    }

    private boolean isRoadFree(Road road) {
        if (road.getOnRoad().isEmpty()) return true;
        Vehicle closestCar = road.getOnRoad().getFirst();
        double distance = road.getStartPoint2D().distance(closestCar.getPosition());
        return distance > 30;
    }

    private void createVehicleOnRoad(Road road) {
        Vehicle car;
        if (shouldISpawnTruck()) {
            car = new Vehicle(road.getStartPoint2D(), new Limitation(truckLimits), 5, 20, road);
            car.setType(VehicleType.TRUCK);
        }
        else {
            car = new Vehicle(road.getStartPoint2D(), new Limitation(carLimits), 5, 8, road);
            car.setType(VehicleType.CAR);
        }
        double startingV = Optional.ofNullable(road.getOnRoad().peekFirst())
                .map(Vehicle::getSpeed)
                .orElse(car.getLimits().getMaxVel());
        car.setVelocity(startingV);
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
