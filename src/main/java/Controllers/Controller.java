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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import model.car.Car;
import model.road.Road;
import repository.CarRepository;
import repository.CarRepositoryImpl;
import repository.RoadRepository;
import repository.RoadRepositoryImpl;

public class Controller {
    private static final int LINE_NUMBER = 3;

    @FXML
    private Pane sim;
    @FXML
    private Button startButton;
    @FXML
    private Slider spawnRateSlider;
    @FXML
    private Label spawnRateLabel;

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

    private void roadObjectsInit() {
        Road road = roadService.getRoadRepo().byId(1L);
        roadObjectService.createCarCounter(road);
        roadObjectService.createVehicleSpawner(roadService.getRoadRepo().getAll().toArray(new Road[0]));
        connectSpawnRatio();
        sim.getChildren().addAll(roadObjectService.getAllViews());
    }

    private void connectSpawnRatio() {
        spawnRateSlider.valueProperty().addListener((obs, oldval, newVal) ->
                spawnRateSlider.setValue(newVal.intValue()));
        spawnRateLabel.textProperty().bind(spawnRateSlider.valueProperty().asString());
        roadObjectService.getSpawner().vehiclePerHourProperty().bindBidirectional(spawnRateSlider.valueProperty());
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
}