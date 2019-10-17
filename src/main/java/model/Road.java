package model;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
public class Road extends Line {
    static Long count = 1L;
    @ToString.Exclude
    private final Long roadId;

    Point2D direction;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Road nextRoad;

    public Road(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
        roadId = count++;
        direction = getEndPoint2D().subtract(getStartPoint2D()).normalize();
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