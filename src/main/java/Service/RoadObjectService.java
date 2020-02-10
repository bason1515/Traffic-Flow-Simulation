package Service;

import javafx.scene.Group;
import lombok.Getter;
import lombok.Setter;
import model.road.Road;
import model.roadObject.CarCounter;
import model.roadObject.VehicleSpawner;
import repository.CarRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class RoadObjectService {
    private CarRepository carRepository;
    private ArrayList<CarCounter> carCounter;
    private ArrayList<VehicleSpawner> spawner;

    public RoadObjectService(CarRepository carRepository) {
        this.carRepository = carRepository;
        this.carCounter = new ArrayList<>();
        this.spawner = new ArrayList<>();
    }

    public void createCarCounter(Road road) {
        carCounter.add(new CarCounter(road));
    }

    public VehicleSpawner createVehicleSpawner(Road road) {
        VehicleSpawner spawner = new VehicleSpawner(carRepository);
        spawner.addRoad(road);
        this.spawner.add(spawner);
        return spawner;
    }

    public void updateRoadObjects(long elapsedTime) {
        double elapsedSeconds = elapsedTime / 1_000_000_000.0;
        carCounter.forEach(c -> c.update(carRepository.getAll(), elapsedSeconds));
        spawner.forEach(s -> s.update(elapsedSeconds));
    }

    public List<Group> getAllViews() {
        return carCounter.stream().map(CarCounter::getView).collect(Collectors.toList());
    }
}
