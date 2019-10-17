package Service;

import lombok.Data;
import model.Car;
import model.Road;
import repository.CarRepository;
import repository.RoadRepository;

import java.util.logging.Logger;

@Data
public class SimulationService {
    private RoadRepository roads;
    private CarRepository cars;
    private final Logger logger;

    public SimulationService(RoadRepository roadRepository, CarRepository carRepository) {
        logger = Logger.getLogger("Service");
        roads = roadRepository;
        cars = carRepository;
        logger.info("Starting simulation service");
    }

    public void addCar(Car car) {
        logger.info("Add car" + car.getPosition());
        cars.save(car);
    }

    public void addRoad(Road road) {
        logger.info("Add " + road);
        roads.save(road);
    }

    public void updateCars(){
        cars.getAll().forEach(car -> {
            car.drive();
        });
    }

}
