package model.roadObject;

import model.road.Road;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.CarRepositoryImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VehicleSpawnerTest {
    Road road;
    VehicleSpawner spawner;

    @BeforeEach
    void setUp() {
        road = new Road(0, 0, 50, 50);
        spawner = new VehicleSpawner(new CarRepositoryImpl());
        spawner.addRoad(road);
        spawner.setVehiclePerHour(3600);
    }

    @Test
    void addRoad() {
        assertEquals(road, spawner.getRoads().get(0));
    }

    @Test
    void removeRoad() {
        spawner.removeRoads(road);
        assertEquals(0, spawner.getRoads().size());
    }

    @Test
    void shouldNotSpawnIfNotTimeUp() {
        spawner.update(0.5);
        assertEquals(0, road.getOnRoad().size());
    }

    @Test
    void shouldSpawnIfTimeUp() {
        spawner.update(1);
        assertEquals(1, road.getOnRoad().size());
    }

    @Test
    void shouldNotSpawnOnAllRoads() {
        Road road2 = new Road(1, 1, 2, 2);
        Road road3 = new Road(3, 3, 4, 4);
        spawner.addRoad(road2);
        spawner.addRoad(road3);
        spawner.update(2);
        assertEquals(1, road.getOnRoad().size());
        assertEquals(1, road2.getOnRoad().size());
        assertEquals(0, road3.getOnRoad().size());
    }

    @Test
    void shouldNotSpawnIfNoEmptyRoad() {
        Road road2 = new Road(1, 1, 2, 2);
        Road road3 = new Road(3, 3, 4, 4);
        spawner.addRoad(road2);
        spawner.addRoad(road3);
        spawner.update(2);
        spawner.update(2);
        assertEquals(1, road.getOnRoad().size());
        assertEquals(1, road2.getOnRoad().size());
        assertEquals(1, road3.getOnRoad().size());
    }

}