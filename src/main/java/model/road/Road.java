package model.road;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import lombok.Getter;
import lombok.Setter;
import model.car.Car;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

@Getter
@Setter
public class Road extends Line {
    public static final int LINE_OFFSET = 10;

    private static Long count = 1L;
    private final Long roadId;

    private Point2D direction;
    private double lenght;
    private LinkedList<Car> onRoad;
    private List<Car> bufferOnRoad;
    private Road left = null;
    private Road right = null;

    public Road(Point2D start, Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public Road(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
        roadId = count++;
        direction = getEndPoint2D().subtract(getStartPoint2D()).normalize();
        lenght = getStartPoint2D().distance(getEndPoint2D());
        onRoad = new LinkedList<>();
        bufferOnRoad = new ArrayList<>();
    }

    public void removeOnRoad(Car c) {
        onRoad.remove(c);
    }

    public void addOnRoad(Car car) {
        ListIterator<Car> iterator = onRoad.listIterator();
        while (iterator.hasNext()) {
            Car target = iterator.next();
            if (!isBehind(car, target)) {
                iterator.previous();
                iterator.add(car);
                return;
            }
        }
        iterator.add(car);
    }

    private boolean isBehind(Car source, Car target) {
        if (source.equals(target)) return true;
        Point2D driveVec = direction;
        Point2D vecToTarget = new Point2D(target.getX() - source.getX(), target.getY() - source.getY());
        double angle = driveVec.angle(vecToTarget);
        return angle >= 90;
    }

    public void addLine() {
        if (left != null)
            left.addLine();
        else createNewLine();
    }

    private void createNewLine() {
        Road newLine = createLineWithOffset();
        connectToLeft(newLine);
    }

    private Road createLineWithOffset() {
        Point2D offset = new Point2D(direction.getY(), direction.getX() * -1);
        offset = offset.multiply(Road.LINE_OFFSET);
        return new Road(getStartPoint2D().add(offset), getEndPoint2D().add(offset));
    }

    private void connectToLeft(Road newRoad) {
        newRoad.setRight(this);
        setLeft(newRoad);
    }

    public List<Road> getAllLines() {
        List<Road> list = new ArrayList<>();
        inOrder(this, list);
        return list;
    }

    private void inOrder(Road focus, List<Road> list) {
        if (focus.getLeft() != null) {
            inOrder(focus.getLeft(), list);
        }
        list.add(focus);
    }

    public Point2D getPointOnLine(double distFromStart) {
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

    public Point2D getDriveDirection() {
        return direction;
    }

}