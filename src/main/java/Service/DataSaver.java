package Service;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import model.roadObject.CarCounter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.TimerTask;

public class DataSaver extends TimerTask {
    private ReadOnlyDoubleWrapper carInput;
    private CarCounter carCounter;

    public DataSaver(DoubleProperty carInput, CarCounter carCounter) {
        this.carInput = new ReadOnlyDoubleWrapper(this, "carInput");
        this.carInput.bind(carInput);
        this.carCounter = carCounter;
    }

    @Override
    public void run() {
        try {
            save();
        } catch (IOException e) {
            System.out.println("Could not save data to file");
        }
    }

    public void save() throws IOException {
        FileWriter fileWriter = new FileWriter("data.txt", true);
        String data = carInput.get() + " " + carCounter.getThroughputPerHour() + "\n";
        fileWriter.append(data);
        fileWriter.close();
    }

}
