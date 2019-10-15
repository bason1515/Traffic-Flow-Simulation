package repository;

import model.Car;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CarRepositoryImpl implements CarRepository{
    private Map<Long, Car> cars;

    public CarRepositoryImpl(){
        cars = new HashMap<>();
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
}
