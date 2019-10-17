package Controllers;

import Service.SimulationService;
import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import model.Car;
import model.Road;
import repository.CarRepositoryImpl;
import repository.RoadRepositoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Controller {
    @FXML
    private Pane sim;

    private SimulationService simServ;

    @FXML
    public void initialize() {
        simServ = new SimulationService(new RoadRepositoryImpl(), new CarRepositoryImpl());
        Road road1 = new Road(100, 100, 200, 200);
        Road road2 = new Road(200, 200, 350, 180);
        Road road3 = new Road(350, 180, 100, 100);
        road1.setNextRoad(road2);
        road2.setNextRoad(road3);
        road3.setNextRoad(road1);
        simServ.addRoad(road1);
        simServ.addRoad(road2);
        simServ.addRoad(road3);
        createCars(5).forEach(simServ::addCar);

        sim.getChildren().addAll(simServ.getRoads().getAll());
        sim.getChildren().addAll(simServ.getCars().getAll());

        startAnimation();
    }

    private List<Car> createCars(int num) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        ArrayList<Car> result = new ArrayList<>();
        int size = simServ.getRoads().getAll().size();
        for (int i = 0; i < num; i++) {
            Car c = new Car(5, 5, simServ.getRoads().byId(rng.nextLong(1, size + 1)));
            Road road = c.getCurrentRoad();
            double length = road.getStartPoint2D().subtract(road.getEndPoint2D()).magnitude();
            Point2D pos = road.getDirection().multiply(rng.nextDouble(length)).add(road.getStartPoint2D());
            c.setPosition(pos);
            result.add(c);
        }
        return result;
    }

    private void startAnimation() {
        final LongProperty lastUpdateTime = new SimpleLongProperty(0);
        final AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long timestamp) {
                if (lastUpdateTime.get() > 0) {
                    long elapsedTime = timestamp - lastUpdateTime.get();
                    simServ.updateCars();
                }
                lastUpdateTime.set(timestamp);
            }

        };
        timer.start();
    }
}
