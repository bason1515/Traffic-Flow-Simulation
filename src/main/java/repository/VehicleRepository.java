package repository;

import javafx.collections.MapChangeListener;
import model.vehicle.Vehicle;
import model.vehicle.VehicleType;

import java.util.Collection;
import java.util.List;

public interface VehicleRepository {

    void save(Vehicle car);

    void remove(Long id);

    Collection<Vehicle> getAll();

    Vehicle byId(Long id);

    List<Vehicle> byCarType(VehicleType type);

    void addListener(MapChangeListener listener);

    void removeListener(MapChangeListener listener);
}
