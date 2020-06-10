package Service;

import javafx.scene.Group;
import lombok.Getter;
import lombok.Setter;
import model.road.Road;
import model.roadObject.VehicleCounter;
import model.roadObject.VehicleSpawner;
import repository.VehicleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class RoadObjectService {
    private VehicleRepository vehicleRepository;
    private ArrayList<VehicleCounter> vehicleCounter;
    private ArrayList<VehicleSpawner> spawner;

    public RoadObjectService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleCounter = new ArrayList<>();
        this.spawner = new ArrayList<>();
    }

    public void createCarCounter(Road road, double position) {
        vehicleCounter.add(new VehicleCounter(road, position));
    }

    public VehicleSpawner createVehicleSpawner(Road road) {
        VehicleSpawner spawner = new VehicleSpawner(vehicleRepository);
        spawner.addRoad(road);
        this.spawner.add(spawner);
        return spawner;
    }

    public void updateRoadObjects(long elapsedTime) {
        double elapsedSeconds = elapsedTime / 1_000_000_000.0;
        vehicleCounter.forEach(c -> c.update(vehicleRepository.getAll(), elapsedSeconds));
        spawner.forEach(s -> s.update(elapsedSeconds));
    }

    public List<Group> getAllViews() {
        return vehicleCounter.stream().map(VehicleCounter::getView).collect(Collectors.toList());
    }

    public void restart() {
        vehicleCounter.forEach(VehicleCounter::reset);
        spawner.forEach(s -> s.setTotalSpawnedVehicles(0));
    }
}
