package Service;

import javafx.scene.Node;
import model.road.Road;
import model.roadObject.CarCounter;
import model.roadObject.VehicleSpawner;
import repository.CarRepository;

public class RoadObjectService {
    CarRepository carRepository;
    CarCounter carCounter;
    VehicleSpawner spawner;

    public RoadObjectService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public void createCarCounter(Road road) {
        carCounter = new CarCounter(road);
    }

    public void createVehicleSpawner(Road... roads){
        spawner = new VehicleSpawner(carRepository);
        spawner.addRoads(roads);
        spawner.setVehiclePerMinute(500);
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
