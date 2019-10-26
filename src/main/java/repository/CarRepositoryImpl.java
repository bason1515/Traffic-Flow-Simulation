package repository;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import model.car.Car;

import java.util.Collection;

public class CarRepositoryImpl implements CarRepository {
    private ObservableMap<Long, Car> cars;

    public CarRepositoryImpl() {
        cars = FXCollections.observableHashMap();
    }

    @Override
    public void save(Car car) {
        cars.put(car.getCarId(), car);
    }

    @Override
    public void remove(Long id) {
        cars.remove(id);
    }

    @Override
    public Collection<Car> getAll() {
        return cars.values();
    }

    @Override
    public Car byId(Long id) {
        return cars.get(id);
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
