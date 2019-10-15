package Service;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Data;
import model.Car;
import model.Road;
import repository.CarRepository;
import repository.RoadRepository;

import java.util.logging.Logger;

@Data
public class SimulationService {
    private RoadRepository roads;
    private CarRepository cars;
    Logger logger;

    public SimulationService(RoadRepository roadRepository, CarRepository carRepository) {
        logger = Logger.getLogger("Service");
        roads = roadRepository;
        cars = carRepository;
        logger.info("Starting simulation service");
    }

    public void addCar(Car car) {
        logger.info("Add car" + car.getPosition());
        cars.save(car);
    }

    public void addRoad(Road road) {
        logger.info("Add " + road);
        roads.save(road);
    }

    public void updateCars(){
        cars.getAll().forEach(car -> car.drive(car.getCurrentRoad()));
    }

    public void drawRoad(GraphicsContext gc) {
        gc.beginPath();
        gc.setLineWidth(5);
        gc.setStroke(Color.BLUE);
        for (Road r : roads.getAll()) {
            gc.moveTo(r.getStartX(), r.getStartY());
            gc.lineTo(r.getEndX(), r.getEndY());
        }
        gc.stroke();
        gc.closePath();
    }

    public void drawCar(GraphicsContext gc) {
        gc.beginPath();
        gc.setLineWidth(3);
        gc.setStroke(Color.RED);
        for (Car c : cars.getAll()) {
            gc.rect(c.getX(), c.getY(), c.getWidth(), c.getHeight());
        }
        gc.stroke();
        gc.closePath();
    }

}
