package Controllers;

import Controllers.event.RestartEvent;
import Controllers.event.StartStopEvent;
import Service.DataSaver;
import Service.RoadObjectService;
import Service.RoadService;
import Service.VehicleService;
import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Getter;
import model.road.Road;
import model.road.RoadType;
import model.roadObject.VehicleSpawner;
import model.vehicle.Limitation;
import model.vehicle.Obstacle;
import model.vehicle.Vehicle;
import repository.RoadRepository;
import repository.RoadRepositoryImpl;
import repository.VehicleRepository;
import repository.VehicleRepositoryImpl;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SimulationController {
    private static final int LANE_NUMBER = 1;

    @FXML
    private Pane sim;
    @FXML
    private Button startButton, restartButton;
    @FXML
    private Slider spawnRateSlider, truckChanceSlider, maxAcceSlider, maxVeloSlider,
            maxTruckAcceSlider, maxTruckVeloSlider, timewarpSlider, rampSpawnRateSlider;
    @FXML
    private Label spawnRateLabel, truckChanceLabel, maxCarAcceLabel, maxCarVeloLabel,
            maxTruckAcceLabel, maxTruckVeloLabel, timewarpLabel, rampSpawnRateLabel,
            totalTimeLabel;
    @FXML
    private SimulationMenuBar menuBar;

    private DoubleProperty simTotalSec;
    private VehicleService vehicleService;
    private RoadService roadService;
    private RoadObjectService roadObjectService;
    private DataSaver dataSaver;

    AnimationTimer simulationTimer;

    public void shutdown() {
    }

    @FXML
    public void initialize() {
        RoadRepository roadRepository = new RoadRepositoryImpl();
        VehicleRepository vehicleRepository = new VehicleRepositoryImpl();
        roadService = new RoadService(roadRepository);
        vehicleService = new VehicleService(vehicleRepository, roadRepository);
        roadObjectService = new RoadObjectService(vehicleRepository);
        roadsInit();
        initRamp(200);
        roadObjectsInit();
        sim.getChildren().addAll(roadService.getRoadRepo().getAll());
        vehicleService.getCarRepo().addListener((MapChangeListener<Long, Vehicle>) change -> {
            if (change.wasAdded()) {
                Vehicle addedCar = change.getValueAdded();
                sim.getChildren().add(addedCar.getView());
            }
            if (change.wasRemoved()) {
                Vehicle removedCar = change.getValueRemoved();
                sim.getChildren().remove(removedCar.getView());
            }
        });
        startButton.setOnAction(new StartStopEvent(startButton, this));
        restartButton.setOnAction(new RestartEvent(restartButton, this));
        initSimTimeLabel();
        dataSaver = new DataSaver(this);
        menuBar.setDataSaver(dataSaver);
    }

    private void initSimTimeLabel() {
        simTotalSec = new SimpleDoubleProperty(this, "simTotalSec", 0.0);
        totalTimeLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
        totalTimeLabel.textProperty().bind(simTotalSec.asString("Sim time: %.2f s"));
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
        ramp.setType(RoadType.RAMP);
        ramp.setStartPoint2D(ramp.getStartPoint2D().add(ramp.getDirection().multiply(ramp.getLength() / 2 + 50)));
        ramp.setEndPoint2D(ramp.getStartPoint2D());
        ramp.setEndPoint2D(ramp.getEndPoint2D().add(ramp.getDirection().multiply(length)));
        createObstacleAtEnd(ramp);
        createInFlowRoad(ramp);
        roadService.addRoad(ramp);
    }

    private void createObstacleAtEnd(Road ramp) {
        Point2D position = ramp.getEndPoint2D().subtract(ramp.getDirection());
        Obstacle obstacle = new Obstacle(position,
                new Limitation(0.0, 0.0, 0.0), 5, 5, ramp);
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
        roadObjectService.createCarCounter(road, 0.28);
        roadObjectService.createCarCounter(road, 0.79);
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
                    increaseSimTime(elapsedTime);
                    roadObjectService.updateRoadObjects(elapsedTime);
                    vehicleService.updateCars(elapsedTime);
                }
                lastUpdateTime.set(timestamp);
            }
        };
        simulationTimer.start();
    }

    private void increaseSimTime(long elapsedTime) {
        double elapsedTimeSec = elapsedTime / 1_000_000_000.0;
        simTotalSec.setValue(simTotalSec.getValue() + elapsedTimeSec);
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

    public void restart() {
        vehicleService.restart();
        roadService.restart();
        roadService.getRoadRepo().getAll().stream()
                .filter(r -> r.getType() == RoadType.RAMP)
                .forEach(this::createObstacleAtEnd);
        roadObjectService.restart();
        simTotalSec.setValue(0.0);
        dataSaver.clearData();
    }
}