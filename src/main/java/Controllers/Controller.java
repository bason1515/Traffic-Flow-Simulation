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
    private static final int CAR_NUMBER = 50;
    private static final int LINE_NUMBER = 3;
    private static final double MIN_VELO = 75;
    private static final double MAX_VELO = 95;
    private static final double MIN_ACCE = 0.9;
    private static final double MAX_ACCE = 1.2;
    private static final double BREAK_SPEED = -0.1;

    private static final int TRUCK_NUMBER = 15;
    private static final double TMIN_VELO = 50;
    private static final double TMAX_VELO = 55;
    private static final double TMIN_ACCE = 0.5;
    private static final double TMAX_ACCE = 0.6;
    private static final double TBREAK_SPEED = -0.07;

    @FXML
    private Pane sim;

    private SimulationService simServ;

    @FXML
    public void initialize() {
        simServ = new SimulationService(new RoadRepositoryImpl(), new CarRepositoryImpl());
        Road road1 = new Road(50, 50, 800, 800);
        simServ.addRoad(road1);
        for (int i = 0; i < LINE_NUMBER; i++) {
            simServ.addLine(road1.getRoadId());
        }
        createCars(CAR_NUMBER, MIN_VELO, MAX_VELO, MIN_ACCE, MAX_ACCE, BREAK_SPEED, 8).forEach(simServ::addCar);
        createCars(TRUCK_NUMBER, TMIN_VELO, TMAX_VELO, TMIN_ACCE, TMAX_ACCE, TBREAK_SPEED, 20).forEach(simServ::addCar);

        sim.getChildren().addAll(simServ.getRoadRepo().getAll());
        sim.getChildren().addAll(simServ.getCarsView());

        startAnimation();
    }

    private void addMouseScroll() {
        sim.setOnScroll(event -> {
            double zoomFactor = 1.05;
            double deltaY = event.getDeltaY();
            if (deltaY < 0) {
                zoomFactor = 2.0 - zoomFactor;
            }
            sim.setScaleX(sim.getScaleX() * zoomFactor);
            sim.setScaleY(sim.getScaleY() * zoomFactor);
        });
    }

    private List<Car> createCars(int num, double minVelo, double maxVelo, double minAcce, double maxAcce, double maxBreak, double carLength) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        ArrayList<Car> result = new ArrayList<>();
        int size = simServ.getRoadRepo().getAll().size();
        for (int i = 0; i < num; i++) {
            Road randRoad = simServ.getRoadRepo().byId(rng.nextLong(1, size + 1));
            double length = randRoad.getStartPoint2D().subtract(randRoad.getEndPoint2D()).magnitude();
            Point2D pos = randRoad.getDirection().multiply(rng.nextDouble(length)).add(randRoad.getStartPoint2D());
            double randVelo = rng.nextDouble(minVelo, maxVelo);
            double randAccel = rng.nextDouble(minAcce, maxAcce);
            Car c = new Car(pos.getX(), pos.getY(), 5, carLength, randRoad, randAccel, maxBreak, randVelo);
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
                    simServ.updateSim(elapsedTime);
                }
                lastUpdateTime.set(timestamp);
            }

        };
        timer.start();
    }
}