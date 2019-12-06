package Controllers.event;

import Controllers.Controller;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class StartStopEvent implements EventHandler<ActionEvent> {
    private boolean started;
    private Button button;
    private Controller simulation;

    public StartStopEvent(Button button, Controller simulation) {
        this.started = false;
        this.button = button;
        this.simulation = simulation;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        if (started) {
            simulation.stopAnimation();
            button.setText("Start");
        } else {
            simulation.startAnimation();
            button.setText("Stop");
        }
        started = !started;
    }
}
