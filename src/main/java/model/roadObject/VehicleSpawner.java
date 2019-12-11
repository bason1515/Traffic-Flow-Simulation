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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class VehicleSpawner {
    @Getter
    @Setter
    private List<Road> roads;
    private IntegerProperty vehiclePerMinute;
    private ReadOnlyDoubleWrapper spawnRate;
    private double lastSpawnTimeInSec;

    public VehicleSpawner() {
        roads = new ArrayList<>();
        vehiclePerMinute = new SimpleIntegerProperty(this, "spawnRate", 0);
        spawnRate = new ReadOnlyDoubleWrapper(this, "spawnRate");
        lastSpawnTimeInSec = 0;
        bindSpawnRate();
    }

    private void bindSpawnRate() {
        spawnRate.bind(Bindings.createDoubleBinding(() -> {
            if (this.getVehiclePerMinute() == 0) return 0.0;
            return 60.0 / this.getVehiclePerMinute();
        }, vehiclePerMinute));
    }

    public void update(double elapsedSeconds) {
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
        return distance > 50;
    }

    private void createVehicleOnRoad(Road road) {
        Limitation limits = new Limitation(1.5, -1, 80);
        Car car = new Car(road.getStartPoint2D(), limits, 5, 8, road);
        road.addOnRoad(car);
    }

    public void addRoads(Road... roads) {
        this.roads.addAll(Arrays.asList(roads));
    }

    public void removeRoads(Road... roads) {
        this.roads.removeAll(Arrays.asList(roads));
    }

    public int getVehiclePerMinute() {
        return vehiclePerMinute.get();
    }

    public IntegerProperty vehiclePerMinuteProperty() {
        return vehiclePerMinute;
    }

    public void setVehiclePerMinute(int vehiclePerMinute) {
        this.vehiclePerMinute.set(vehiclePerMinute);
    }

}
