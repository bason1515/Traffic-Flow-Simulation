package repository;

import javafx.collections.MapChangeListener;
import model.car.Car;
import model.car.CarType;

import java.util.Collection;
import java.util.List;

public interface CarRepository {

    void save(Car car);

    void remove(Long id);

    Collection<Car> getAll();

    Car byId(Long id);

    List<Car> byCarType(CarType type);

    void addListener(MapChangeListener listener);

    void removeListener(MapChangeListener listener);
}
