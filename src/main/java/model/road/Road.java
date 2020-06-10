package model.road;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Line;
import lombok.Getter;
import lombok.Setter;
import model.vehicle.Vehicle;
import model.vehicle.changeLaneBehavior.ChangeLaneFactory;

import java.util.*;

@Getter
@Setter
public class Road extends Line {
    public static final int LANE_OFFSET = 10;

    private static Long count = 1L;
    private final Long roadId;
    private RoadType type;

    private Point2D direction;
    private double length;
    private LinkedList<Vehicle> onRoad;
    private Road left = null;
    private Road right = null;
    private Road next = null;

    public Road(Point2D start, Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public Road(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
        this.type = RoadType.LANE;
        roadId = count++;
        direction = getEndPoint2D().subtract(getStartPoint2D()).normalize();
        length = getStartPoint2D().distance(getEndPoint2D());
        onRoad = new LinkedList<>();
        roadImage();
    }

    private void roadImage() {
        Image roadImage = new Image("file:src/main/resources/Road.png");
        ImagePattern roadFill = new ImagePattern(roadImage, 5, 0, length, LANE_OFFSET, false);
        this.setStrokeWidth(9);
        this.setStroke(roadFill);
    }

    public void removeOnRoad(Vehicle c) {
        onRoad.remove(c);
    }

    public void addOnRoad(Vehicle car) {
        ListIterator<Vehicle> iterator = onRoad.listIterator();
        while (iterator.hasNext()) {
            Vehicle target = iterator.next();
            if (!isBehind(car, target)) {
                iterator.previous();
                iterator.add(car);
                return;
            }
        }
        iterator.add(car);
    }

    public void moveCarToThisRoad(Vehicle car) {
        car.getCurrentRoad().removeOnRoad(car);
        addOnRoad(car);
        car.setCurrentRoad(this);
        car.setDirection(direction);
        car.getDriver().getWiedemann().setDrivenRoad(this);
        car.getDriver().setChangeLane(ChangeLaneFactory.createChangeLane(car));
    }

    private boolean isBehind(Vehicle source, Vehicle target) {
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
        list.add(focus);
        focus.getLeft().ifPresent(road -> inOrderLeft(road, list));
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

    public Optional<Road> getLeft() {
        return Optional.ofNullable(left);
    }

    public Optional<Road> getRight() {
        return Optional.ofNullable(right);
    }

    public Optional<Road> getNext() {
        return Optional.ofNullable(next);
    }

}