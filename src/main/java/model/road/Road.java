package model.road;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import lombok.Getter;
import lombok.Setter;
import model.car.Car;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Road extends Line {
    public static final int LINE_OFFSET = 10;

    private static Long count = 1L;
    private final Long roadId;

    private Point2D direction;
    private List<Car> onRoad;
    private Road left = null;
    private Road right = null;

    public Road(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
        roadId = count++;
        direction = getEndPoint2D().subtract(getStartPoint2D()).normalize();
        onRoad = new ArrayList<>();
    }

    public void addOnRoad(Car c){
        onRoad.add(c);
    }

    public void removeOnRoad(Car c){
        onRoad.remove(c);
    }

    public Point2D getPointOnLine(double distFromStart){
        return this.getDirection().multiply(distFromStart).add(this.getStartPoint2D());
    }

    public Point2D getStartPoint2D() {
        return new Point2D(getStartX(), getStartY());
    }

    public Point2D getEndPoint2D() {
        return new Point2D(getEndX(), getEndY());
    }

    public void setStartPoint2D(Point2D start) {
        setStartX(start.getX());
        setStartY(start.getY());
    }

    public void setEndPoint2D(Point2D end) {
        setEndX(end.getX());
        setEndY(end.getY());
    }

    public Point2D getDriveDirection(Point2D position) {
        return direction;
    }

}