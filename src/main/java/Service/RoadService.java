package Service;

import lombok.Getter;
import lombok.Setter;
import model.road.Road;
import repository.RoadRepository;

@Getter
@Setter
public class RoadService {
    private RoadRepository roadRepo;

    public RoadService(RoadRepository roadRepository) {
        this.roadRepo = roadRepository;
    }

    public void addLinesToRoad(Road target, int amount) {
        for (int i = 0; i < amount; i++) {
            target.addLine();
        }
    }

    public void addRoad(Road road) {
        road.getAllLines().forEach(roadRepo::save);
    }

    public void removeRoad(Long id) {
        roadRepo.byId(id).getAllLines().forEach(r -> roadRepo.remove(r.getRoadId()));
    }

}
