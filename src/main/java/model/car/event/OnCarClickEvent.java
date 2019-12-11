package model.car.event;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.car.Car;

public class OnCarClickEvent implements EventHandler<MouseEvent> {

    private static Car lastCarClicker = null;
    private final Car car;

    public OnCarClickEvent(Car car) {
        this.car = car;
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getSource() instanceof Rectangle) {
            Rectangle source = (Rectangle) event.getSource();
            if (lastCarClicker != null) lastCarClicker.getView().setFill(Color.BLACK);
            lastCarClicker = car;
            car.getView().setFill(Color.RED);
            System.out.printf("You track car %d%n", car.getCarId());
            disturbeFlow();
        }
    }

    private void disturbeFlow(){
        car.setVelocity(Point2D.ZERO);
    }
}
