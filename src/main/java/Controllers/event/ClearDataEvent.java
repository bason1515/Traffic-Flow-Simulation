package Controllers.event;

import Service.DataSaver;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;

import java.util.Optional;

public class ClearDataEvent implements EventHandler<ActionEvent> {
    private MenuItem menuItem;
    private DataSaver dataSaver;

    public ClearDataEvent(MenuItem menuItem, DataSaver dataSaver) {
        this.menuItem = menuItem;
        this.dataSaver = dataSaver;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear data confirmation");
        alert.setHeaderText("Delete " + dataSaver.getData().size() + " records from memory?");
//        alert.setContentText("Are you ok with this?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK)
            dataSaver.clearData();
    }

}
