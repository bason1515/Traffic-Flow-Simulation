package Controllers;

import Controllers.event.StartStopEvent;
import Service.CarService;
import Service.DataSaver;
import Service.RoadObjectService;
import Service.RoadService;
import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import model.car.Car;
import model.car.Limitation;
import model.road.Road;
import model.roadObject.VehicleSpawner;
import repository.CarRepository;
import repository.CarRepositoryImpl;
import repository.RoadRepository;
import repository.RoadRepositoryImpl;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;

public class Controller {
    private static final int LANE_NUMBER = 1;

    @FXML
    private Pane sim;
    @FXML
    private Button startButton;
    @FXML
    private Slider spawnRateSlider, truckChanceSlider, maxAcceSlider, maxVeloSlider,
            maxTruckAcceSlider, maxTruckVeloSlider, timewarpSlider, rampSpawnRateSlider;
    @FXML
    private Label spawnRateLabel, truckChanceLabel, maxCarAcceLabel, maxCarVeloLabel,
            maxTruckAcceLabel, maxTruckVeloLabel, timewarpLabel, rampSpawnRateLabel;

    private CarService carService;
    private RoadService roadService;
    private RoadObjectService roadObjectService;
    private Timer timer;

    AnimationTimer simulationTimer;

    public void shutdown() {
        if (timer != null)
            timer.cancel();
    }

    @FXML
    public void initialize() {
        RoadRepository roadRepository = new RoadRepositoryImpl();
        CarRepository carRepository = new CarRepositoryImpl();
        roadService = new RoadService(roadRepository);
        carService = new CarService(carRepository, roadRepository);
        roadObjectService = new RoadObjectService(carRepository);
        roadsInit();
        initRamp(200);
        roadObjectsInit();
        sim.getChildren().addAll(roadService.getRoadRepo().getAll());
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
        roadService.addLanesToRoad(road1, LANE_NUMBER);
        roadService.addRoad(road1);
    }

    private void initRamp(double length) {
        Road ramp;
        Road target = roadService.getRoadRepo().getAll().stream()
                .filter(road -> !road.getRight().isPresent())
                .findFirst()
                .orElse(roadService.getRoadRepo().byId(1L));
        target.addLaneToRight();
        ramp = target.getRight().get();
        target.setRight(null);
        ramp.setStartPoint2D(ramp.getStartPoint2D().add(ramp.getDirection().multiply(ramp.getLength() / 2 + 50)));
        ramp.setEndPoint2D(ramp.getStartPoint2D());
        ramp.setEndPoint2D(ramp.getEndPoint2D().add(ramp.getDirection().multiply(length)));
        createObstacleAtEnd(ramp);
        createInFlowRoad(ramp);
        roadService.addRoad(ramp);
    }

    private void createObstacleAtEnd(Road ramp) {
        Point2D position = ramp.getEndPoint2D().subtract(ramp.getDirection());
        Car obstacle = new Car(position,
                new Limitation(0, 0, 0), 5, 5, ramp);
        ramp.addOnRoad(obstacle);
    }

    private Road createInFlowRoad(Road ramp) {
        Road inFlow;
        Point2D direction = ramp.getDirection();
        Point2D start = ramp.getStartPoint2D();
        double rx = (direction.getX() * Math.cos(10)) - (direction.getY() * Math.sin(10));
        double ry = (direction.getX() * Math.sin(10)) + (direction.getY() * Math.cos(10));
        direction = new Point2D(ry, rx);
        start = start.add(direction.multiply(250));
        inFlow = new Road(start, ramp.getStartPoint2D());
        inFlow.setNext(ramp);
        roadObjectService.createVehicleSpawner(inFlow);
        roadService.getRoadRepo().save(inFlow);
        return inFlow;
    }

    private void roadObjectsInit() {
        Road road = roadService.getRoadRepo().byId(1L);
        roadObjectService.createCarCounter(road);
        roadObjectService.createVehicleSpawner(roadService.getRoadRepo().byId(1L));
        addSpawnerGui();
        sim.getChildren().addAll(roadObjectService.getAllViews());
    }

    private void addSpawnerGui() {
        DecimalFormat df = new DecimalFormat("#.##");
        formatSliderDecimalValue(df, truckChanceSlider);
        df = new DecimalFormat("#.#");
        formatSliderDecimalValue(df, maxAcceSlider, maxTruckAcceSlider);
        slidersValueToInt(spawnRateSlider, maxVeloSlider, maxTruckVeloSlider, timewarpSlider, rampSpawnRateSlider);
        bindLabelToSlider(spawnRateLabel, spawnRateSlider);
        bindLabelToSlider(truckChanceLabel, truckChanceSlider);
        bindLabelToSlider(maxCarAcceLabel, maxAcceSlider);
        bindLabelToSlider(maxCarVeloLabel, maxVeloSlider);
        bindLabelToSlider(maxTruckAcceLabel, maxTruckAcceSlider);
        bindLabelToSlider(maxTruckVeloLabel, maxTruckVeloSlider);
        bindLabelToSlider(timewarpLabel, timewarpSlider);
        bindLabelToSlider(rampSpawnRateLabel, rampSpawnRateSlider);
        bindSlidersToSpawner();
    }

    private void bindSlidersToSpawner() {
        roadObjectService.getSpawner().get(1).vehiclePerHourProperty().bindBidirectional(spawnRateSlider.valueProperty());
        roadObjectService.getSpawner().get(0).vehiclePerHourProperty().bindBidirectional(rampSpawnRateSlider.valueProperty());
        roadObjectService.getSpawner().stream()
                .map(VehicleSpawner::truckChanceProperty)
                .forEach(prop -> prop.bindBidirectional(truckChanceSlider.valueProperty()));
        bindCarLimitation();
        bindTruckLimitation();
    }

    private void bindLabelToSlider(Label label, Slider slider) {
        label.textProperty().bind(slider.valueProperty().asString());
    }

    private void slidersValueToInt(Slider... sliders) {
        for (Slider slider : sliders)
            slider.valueProperty().addListener((obs, oldval, newVal) ->
                    slider.setValue(newVal.intValue()));
    }

    private void formatSliderDecimalValue(DecimalFormat df, Slider... sliders) {
        for (Slider slider : sliders)
            slider.valueProperty().addListener((obs, oldVal, newVal) ->
                    slider.setValue(Double.parseDouble(df.format(newVal))));
    }

    private void bindCarLimitation() {
        List<Limitation> limitationList = roadObjectService.getSpawner().stream()
                .map(VehicleSpawner::getCarLimits)
                .collect(Collectors.toList());
        for (Limitation limitation : limitationList) {
            limitation.maxAccelProperty().bindBidirectional(maxAcceSlider.valueProperty());
            limitation.maxVelProperty().bindBidirectional(maxVeloSlider.valueProperty());
        }
    }

    private void bindTruckLimitation() {
        List<Limitation> limitationList = roadObjectService.getSpawner().stream()
                .map(VehicleSpawner::getTruckLimits)
                .collect(Collectors.toList());
        for (Limitation limitation : limitationList) {
            limitation.maxAccelProperty().bindBidirectional(maxTruckAcceSlider.valueProperty());
            limitation.maxVelProperty().bindBidirectional(maxTruckVeloSlider.valueProperty());
        }
    }

    public void startAnimation() {
        final LongProperty lastUpdateTime = new SimpleLongProperty(0);
        simulationTimer = new AnimationTimer() {
            @Override
            public void handle(long timestamp) {
                if (lastUpdateTime.get() > 0) {
                    long elapsedTime = timestamp - lastUpdateTime.get();
                    elapsedTime *= timewarpSlider.getValue();
                    roadObjectService.updateRoadObjects(elapsedTime);
                    carService.updateCars(elapsedTime);
                }
                lastUpdateTime.set(timestamp);
            }
        };
        simulationTimer.start();
        DataSaver task = new DataSaver(spawnRateSlider.valueProperty(), roadObjectService.getCarCounter().get(0));
        timer = new Timer("Data Save");
        timer.scheduleAtFixedRate(task, 5000L, 5000L);
    }

    public void stopAnimation() {
        simulationTimer.stop();
        timer.cancel();
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