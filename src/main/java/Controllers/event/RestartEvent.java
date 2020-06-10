package Controllers.event;

import Controllers.SimulationController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class RestartEvent implements EventHandler<ActionEvent> {
    private Button button;
    private SimulationController simulation;

    public RestartEvent(Button button, SimulationController simulation) {
        this.button = button;
        this.simulation = simulation;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        simulation.restart();
    }
}
