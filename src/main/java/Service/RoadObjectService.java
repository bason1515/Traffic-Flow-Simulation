package Service;

import javafx.scene.Node;
import lombok.Getter;
import lombok.Setter;
import model.road.Road;
import model.roadObject.CarCounter;
import model.roadObject.VehicleSpawner;
import repository.CarRepository;

@Getter
@Setter
public class RoadObjectService {
    private CarRepository carRepository;
    private CarCounter carCounter;
    private VehicleSpawner spawner;

    public RoadObjectService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public void createCarCounter(Road road) {
        carCounter = new CarCounter(road);
    }

    public void createVehicleSpawner(Road road) {
        spawner = new VehicleSpawner(carRepository);
        spawner.addRoad(road);
    }

    public void updateRoadObjects(long elapsedTime) {
        double elapsedSeconds = elapsedTime / 1_000_000_000.0;
        carCounter.update(carRepository.getAll(), elapsedSeconds);
        spawner.update(elapsedSeconds);
    }

    public Node getAllViews() {
        return carCounter.getView();
    }
}
