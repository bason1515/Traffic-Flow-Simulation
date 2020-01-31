package model.road;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import lombok.Getter;
import lombok.Setter;
import model.car.Car;

import java.util.*;

@Getter
@Setter
public class Road extends Line {
    public static final int LANE_OFFSET = 10;

    private static Long count = 1L;
    private final Long roadId;

    private Point2D direction;
    private double length;
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
        length = getStartPoint2D().distance(getEndPoint2D());
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

    public void addLaneToLeft() {
        Road target = left;
        if (target != null)
            target.addLaneToLeft();
        else createLeftLane();
    }

    private void createLeftLane() {
        Road newLane = createLeftLaneWithOffset();
        connectToLeft(newLane);
    }

    private Road createLeftLaneWithOffset() {
        Point2D offset = new Point2D(direction.getY(), direction.getX() * -1);
        offset = offset.multiply(Road.LANE_OFFSET);
        return new Road(getStartPoint2D().add(offset), getEndPoint2D().add(offset));
    }

    private void connectToLeft(Road newRoad) {
        newRoad.setRight(this);
        setLeft(newRoad);
    }

    public void addLaneToRight() {
        Road target = right;
        if (target != null)
            target.addLaneToRight();
        else createRightLane();
    }

    private void createRightLane() {
        Road newLane = createRightLaneWithOffset();
        connectToRight(newLane);
    }

    private Road createRightLaneWithOffset() {
        Point2D offset = new Point2D(direction.getY(), direction.getX() * -1);
        offset = offset.multiply(Road.LANE_OFFSET * -1);
        return new Road(getStartPoint2D().add(offset), getEndPoint2D().add(offset));
    }

    private void connectToRight(Road newRoad) {
        newRoad.setLeft(this);
        setRight(newRoad);
    }

    public List<Road> getAllLanes() {
        List<Road> list = new ArrayList<>();
        inOrderRight(this, list);
        getLeft().ifPresent(l -> inOrderLeft(l, list));
        return list;
    }

    private void inOrderLeft(Road focus, List<Road> list) {
        focus.getLeft().ifPresent(road -> inOrderLeft(road, list));
        list.add(focus);
    }

    private void inOrderRight(Road focus, List<Road> list) {
        focus.getRight().ifPresent(road -> inOrderRight(road, list));
        list.add(focus);
    }

    public Point2D getPointOnLane(double distFromStart) {
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

    public Optional<Road> getLeft() {
        return Optional.ofNullable(left);
    }

    public Optional<Road> getRight() {
        return Optional.ofNullable(right);
    }

}