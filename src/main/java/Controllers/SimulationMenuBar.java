package Controllers;

import Controllers.event.ClearDataEvent;
import Controllers.event.SaveDataEvent;
import Service.DataSaver;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class SimulationMenuBar extends MenuBar{
    MenuItem saveData;
    MenuItem clearData;

    public SimulationMenuBar(){
        super() ;
        this.getMenus().add(filesMenu());
    }

    private Menu filesMenu() {
        Menu menu = new Menu("Files");
        saveData = new MenuItem("Save data");
        clearData = new MenuItem("Clear data");
        menu.getItems().addAll(saveData, clearData);
        return menu;
    }

    public void setDataSaver(DataSaver dataSaver) {
        saveData.setOnAction(new SaveDataEvent(saveData, dataSaver));
        clearData.setOnAction(new ClearDataEvent(clearData, dataSaver));
    }
}

