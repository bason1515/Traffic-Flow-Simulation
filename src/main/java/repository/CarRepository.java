package repository;

import javafx.collections.MapChangeListener;
import model.car.Car;

import java.util.Collection;

public interface CarRepository {

    void save(Car car);

    void remove(Long id);

    Collection<Car> getAll();

    Car byId(Long id);

    void addListener(MapChangeListener listener);

    void removeListener(MapChangeListener listener);
}
