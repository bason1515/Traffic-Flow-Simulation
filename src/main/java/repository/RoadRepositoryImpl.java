package repository;

import model.Road;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RoadRepositoryImpl implements RoadRepository{
    private Map<Long, Road> roads;

    public RoadRepositoryImpl(){
        roads = new HashMap<>();
    }

    @Override
    public void save(Road road) {
        roads.put(road.getRoadId(), road);
    }

    @Override
    public void remove(Long id) {
        roads.remove(id);
    }

    @Override
    public Collection<Road> getAll() {
        return roads.values();
    }

    @Override
    public Road byId(Long id) {
        return roads.get(id);
    }
}
