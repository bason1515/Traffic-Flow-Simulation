package Controllers;

import Controllers.event.StartStopEvent;
import Service.CarService;
import Service.RoadObjectService;
import Service.RoadService;
import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import model.car.Car;
import model.car.CarType;
import model.car.Limitation;
import model.road.Road;
import repository.CarRepository;
import repository.CarRepositoryImpl;
import repository.RoadRepository;
import repository.RoadRepositoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Controller {
    private static final int LINE_NUMBER = 3;

    private static final int CAR_NUMBER = 50;
    private static final double MIN_VELO = 75;
    private static final double MAX_VELO = 95;
    private static final double MIN_ACCE = 0.9;
    private static final double MAX_ACCE = 1.2;
    private static final double BREAK_SPEED = -2;

    private static final int TRUCK_NUMBER = 15;
    private static final double TMIN_VELO = 50;
    private static final double TMAX_VELO = 55;
    private static final double TMIN_ACCE = 0.5;
    private static final double TMAX_ACCE = 0.6;
    private static final double TBREAK_SPEED = -1;

    @FXML
    private Pane sim;
    @FXML
    private Button startButton;

    private CarService carService;
    private RoadService roadService;
    private RoadObjectService roadObjectService;

    AnimationTimer simulationTimer;

    @FXML
    public void initialize() {
        RoadRepository roadRepository = new RoadRepositoryImpl();
        CarRepository carRepository = new CarRepositoryImpl();
        roadService = new RoadService(roadRepository);
        carService = new CarService(carRepository, roadRepository);
        roadObjectService = new RoadObjectService(carRepository);
        roadsInit();
        carsInit();
        roadObjectsInit();
        carService.getCarRepo().addListener((MapChangeListener<Long, Car>) change -> {
            if (change.wasAdded()) {
                Car addedCar = change.getValueAdded();
                sim.getChildren().add(addedCar.getView());
            }
            if (change.wasRemoved()) {
                Car removedCar = change.getValueRemoved();
                sim.getChildren().remove(removedCar.getView());
            }
        });
        startButton.setOnAction(new StartStopEvent(startButton, this));
    }

    private void roadsInit() {
        Road road1 = new Road(50, 50, 800, 600);
        roadService.addLinesToRoad(road1, LINE_NUMBER);
        roadService.addRoad(road1);
        sim.getChildren().addAll(roadService.getRoadRepo().getAll());
    }

    private void carsInit() {
        createCars(CAR_NUMBER, MIN_VELO, MAX_VELO, MIN_ACCE, MAX_ACCE, BREAK_SPEED, 8).forEach(car -> {
            car.setType(CarType.CAR);
            carService.addCar(car);
        });
        createCars(TRUCK_NUMBER, TMIN_VELO, TMAX_VELO, TMIN_ACCE, TMAX_ACCE, TBREAK_SPEED, 20).forEach(car -> {
            car.setType(CarType.TRUCK);
            carService.addCar(car);
        });
        sim.getChildren().addAll(carService.getAllCarsView());
    }

    private void roadObjectsInit() {
        Road road = roadService.getRoadRepo().byId(1L);
        roadObjectService.createCarCounter(road);
        sim.getChildren().addAll(roadObjectService.getAllViews());
    }

    private List<Car> createCars(int num, double minVelo, double maxVelo, double minAcce, double maxAcce, double maxBreak, double carLength) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        ArrayList<Car> result = new ArrayList<>();
        int size = roadService.getRoadRepo().getAll().size();
        for (int i = 0; i < num; i++) {
            Road randRoad = roadService.getRoadRepo().byId(rng.nextLong(1, size + 1));
            double length = randRoad.getStartPoint2D().subtract(randRoad.getEndPoint2D()).magnitude();
            Point2D pos = randRoad.getDirection().multiply(rng.nextDouble(length)).add(randRoad.getStartPoint2D());
            double randVelo = rng.nextDouble(minVelo, maxVelo);
            double randAccel = rng.nextDouble(minAcce, maxAcce);
            Limitation limits = new Limitation(randAccel, randAccel * -1, randVelo);
            Car car = new Car(pos, limits, 5, carLength, randRoad);
            result.add(car);
        }
        return result;
    }

    public void startAnimation() {
        final LongProperty lastUpdateTime = new SimpleLongProperty(0);
        simulationTimer = new AnimationTimer() {
            @Override
            public void handle(long timestamp) {
                if (lastUpdateTime.get() > 0) {
                    long elapsedTime = timestamp - lastUpdateTime.get();
                    roadObjectService.updateRoadObjects(elapsedTime);
                    carService.updateCars(elapsedTime);
                }
                lastUpdateTime.set(timestamp);
            }

        };
        simulationTimer.start();
    }

    public void stopAnimation() {
        simulationTimer.stop();
    }

}