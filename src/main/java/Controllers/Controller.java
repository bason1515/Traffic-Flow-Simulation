package Controllers;

import Service.SimulationService;
import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import model.car.Car;
import model.road.Road;
import repository.CarRepositoryImpl;
import repository.RoadRepositoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Controller {
    private static final int CAR_NUMBER = 100;
    private static final double MIN_VELO = 0.6;
    private static final double MAX_VELO = 1.2;
    private static final double MIN_ACCE = 0.03;
    private static final double MAX_ACCE = 0.06;
    private static final double BREAK_SPEED = -0.1;

    @FXML
    private Pane sim;

    private SimulationService simServ;

    @FXML
    public void initialize() {
        simServ = new SimulationService(new RoadRepositoryImpl(), new CarRepositoryImpl());
        Road road1 = new Road(50, 50, 900, 900);
//        Road road2 = new Road(200, 200, 350, 180);
//        Road road3 = new Road(350, 180, 100, 100);
//        road2.setNextRoad(road3);
//        road3.setNextRoad(road1);
        simServ.addRoad(road1);
        for (int i = 0; i < 3; i++) {
            simServ.addLine(road1.getRoadId());
        }
        createCars(CAR_NUMBER).forEach(simServ::addCar);

        sim.getChildren().addAll(simServ.getRoadRepo().getAll());
        sim.getChildren().addAll(simServ.getCarRepo().getAll());

        startAnimation();
    }

    private List<Car> createCars(int num) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        ArrayList<Car> result = new ArrayList<>();
        int size = simServ.getRoadRepo().getAll().size();
        for (int i = 0; i < num; i++) {
            Car c = new Car(5, 5, simServ.getRoadRepo().byId(rng.nextLong(1, size + 1)));
            c.setMaxVel(rng.nextDouble(MIN_VELO, MAX_VELO));
            c.setMaxAccel(rng.nextDouble(MIN_ACCE, MAX_ACCE));
            c.setMaxBreak(BREAK_SPEED);

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
