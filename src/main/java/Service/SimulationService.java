package Service;

import javafx.geometry.Point2D;
import lombok.Data;
import model.car.Car;
import model.road.Road;
import repository.CarRepository;
import repository.RoadRepository;

@Data
public class SimulationService {
    private RoadRepository roadRepo;
    private CarRepository carRepo;

    public SimulationService(RoadRepository roadRepository, CarRepository carRepository) {
        roadRepo = roadRepository;
        carRepo = carRepository;
    }

    public void addCar(Car car) {
        carRepo.save(car);
    }

    public void addRoad(Road road) {
        roadRepo.save(road);
    }

    public void addLine(Long id) {
        Road r = roadRepo.byId(id);
        if (r.getLeft() != null) {
            addLine(r.getLeft().getRoadId());
        } else {
            Point2D offset = new Point2D(r.getDirection().getY(), r.getDirection().getX() * -1);
            offset = offset.multiply(Road.LINE_OFFSET);
            Road newRoad = new Road(r.getStartX() + offset.getX(), r.getStartY() + offset.getY(),
                    r.getEndX() + offset.getX(), r.getEndY() + offset.getY());
            newRoad.setRight(r);
            r.setLeft(newRoad);
            roadRepo.save(newRoad);
        }
    }

    public void updateCars() {
        carRepo.getAll().forEach(car -> car.drive());
    }

}
