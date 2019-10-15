package Controllers;

import Service.SimulationService;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import model.Car;
import model.Drivable;
import model.Road;
import repository.CarRepositoryImpl;
import repository.RoadRepositoryImpl;

public class Controller {
    @FXML
    private Canvas canvas;

    private SimulationService simServ;

    @FXML
    public void initialize() {
        simServ = new SimulationService(new RoadRepositoryImpl(), new CarRepositoryImpl());
        simServ.addRoad(new Road(100, 100, 200, 200));
        simServ.addRoad(new Road(300, 100, 400, 400));
        simServ.addRoad(new Road(550, 500, 500, 500));
        Drivable d = simServ.getRoads().byId(1L);
        Car c = new Car(50, 50, 3, 3, simServ.getRoads().byId(1L));
        c.setCurrentRoad(d);

        simServ.addCar(c);
//        simServ.addCar(new Car(100, 100, 3, 3, simServ.getRoads().byId(1L)));
//        simServ.addCar(new Car(150, 150, 3, 3, simServ.getRoads().byId(1L)));

        simServ.drawRoad(canvas.getGraphicsContext2D());
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                simServ.updateCars();
                simServ.drawCar(canvas.getGraphicsContext2D());
            }
        };
        timer.start();


    }
}
