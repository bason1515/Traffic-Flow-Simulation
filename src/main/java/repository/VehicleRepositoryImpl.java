package repository;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import model.vehicle.Vehicle;
import model.vehicle.VehicleType;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class VehicleRepositoryImpl implements VehicleRepository {
    private ObservableMap<Long, Vehicle> cars;

    public VehicleRepositoryImpl() {
        cars = FXCollections.observableHashMap();
    }

    @Override
    public void save(Vehicle car) {
        cars.put(car.getCarId(), car);
    }

    @Override
    public void remove(Long id) {
        cars.remove(id);
    }

    @Override
    public Collection<Vehicle> getAll() {
        return cars.values();
    }

    @Override
    public Vehicle byId(Long id) {
        return cars.get(id);
    }

    @Override
    public List<Vehicle> byCarType(VehicleType type) {
        return cars.values().stream().filter(car -> car.getType().equals(type))
                .collect(Collectors.toList());
    }

    @Override
    public void addListener(MapChangeListener listener) {
        cars.addListener(listener);
    }

    @Override
    public void removeListener(MapChangeListener listener) {
        cars.removeListener(listener);
    }
}
