package Controllers.event;

import Service.DataSaver;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SaveDataEvent implements EventHandler<ActionEvent> {
    private MenuItem menuItem;
    private DataSaver dataSaver;

    public SaveDataEvent(MenuItem menuItem, DataSaver dataSaver) {
        this.menuItem = menuItem;
        this.dataSaver = dataSaver;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(menuItem.getParentPopup());
        try {
            dataSaver.saveToFile(file);
        } catch (IOException ex) {
            Logger.getLogger(SaveDataEvent.class.getName())
                    .log(Level.SEVERE, "File save error", ex);
        }
    }

}
