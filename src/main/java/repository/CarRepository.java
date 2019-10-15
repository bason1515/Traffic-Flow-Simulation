package repository;

import model.Car;

import java.util.Collection;

public interface CarRepository {

    void save(Car car);
    void remove(Long id);
    Collection<Car> getAll();
}
