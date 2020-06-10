package model.vehicle.driveBehavior;

import javafx.scene.paint.Color;
import model.vehicle.Vehicle;

public enum VehicleStatus {
    FREE,
    CLOSING_IN,
    BREAK,
    COLLISION,
    FOLLOW;


    public static void setCarColor(Vehicle car) {
        switch (car.getDriver().getStatus()) {
            case FREE:
                car.getView().setFill(Color.LIGHTGREEN);
                break;
            case FOLLOW:
                car.getView().setFill(Color.BLUE);
                break;
            case CLOSING_IN:
                car.getView().setFill(Color.YELLOW);
                break;
            case BREAK:
                car.getView().setFill(Color.RED);
                break;
            case COLLISION:
                car.getView().setFill(Color.DARKRED);
                break;
            default:
                car.getView().setFill(Color.BLACK);
                break;
        }
    }

}
