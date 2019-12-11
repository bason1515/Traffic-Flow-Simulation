package Controllers;

import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public class Camera {
    private DoubleProperty x;
    private DoubleProperty y;
    private Pane simulationPane;
    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;
    private boolean shift;

    public Camera(Pane pane) {
        this.simulationPane = pane;
        pane.setPickOnBounds(true);
        this.x = new SimpleDoubleProperty(this, "x");
        this.y = new SimpleDoubleProperty(this, "y");
//        pane.translateXProperty().bind(Bindings.createDoubleBinding(
//                () -> pane.getTranslateX() + x.getValue(),
//                x, pane.translateXProperty()));
//        pane.translateYProperty().bind(Bindings.createDoubleBinding(
//                () -> pane.getTranslateY() + y.getValue(),
//                y, pane.translateYProperty()));
        simulationPane.getParent().setOnKeyPressed(event -> processKey(event.getCode(), true));
        simulationPane.getParent().setOnKeyReleased(event -> processKey(event.getCode(), false));
        addMouseScroll();
//        startCamera();
    }

    public void startCamera() {
        final LongProperty lastUpdateTime = new SimpleLongProperty(0);
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long timestamp) {
                if (lastUpdateTime.get() > 0) {
                    long elapsedTime = timestamp - lastUpdateTime.get();
                    simulationPane.requestFocus();
                    moveSimulation();
                }
                lastUpdateTime.set(timestamp);
            }
        };
        timer.start();
    }

    private void moveSimulation() {
        int deltaX = 0;
        int deltaY = 0;
        if (left) deltaX += 2;
        if (right) deltaX -= 2;
        if (up) deltaY += 2;
        if (down) deltaY -= 2;
        if (shift){
            deltaX *= 2;
            deltaY *= 2;
        }
        simulationPane.setTranslateX(simulationPane.getTranslateX() + deltaX);
        simulationPane.setTranslateY(simulationPane.getTranslateY() + deltaY);
    }

    private void processKey(KeyCode code, boolean on) {
        switch (code) {
            case LEFT:
                left = on;
                break;
            case RIGHT:
                right = on;
                break;
            case UP:
                up = on;
                break;
            case DOWN:
                down = on;
                break;
            case SHIFT:
                shift = on;
                break;
            default:
                break;
        }
    }

    private void addMouseScroll() {
        System.out.println("Scrolle");
        simulationPane.setOnScroll(event -> {
            double zoomFactor = 1.05;
            double deltaY = event.getDeltaY();
            if (deltaY == 0) return;
            if (deltaY < 0) {
                zoomFactor = 2.0 - zoomFactor;
            }
            simulationPane.setScaleX(simulationPane.getScaleX() * zoomFactor);
            simulationPane.setScaleY(simulationPane.getScaleY() * zoomFactor);
        });
    }

}
