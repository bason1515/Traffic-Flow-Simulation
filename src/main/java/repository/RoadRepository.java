package repository;

import model.road.Road;

import java.util.Collection;

public interface RoadRepository {

    void save(Road road);

    void remove(Long id);

    Collection<Road> getAll();

    Road byId(Long id);
}
