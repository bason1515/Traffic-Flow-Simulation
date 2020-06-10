package Service;

import Controllers.SimulationController;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import lombok.Getter;
import model.roadObject.VehicleCounter;
import model.roadObject.VehicleSpawner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DataSaver {
    private static final int SAVE_TIME = 15;
    private ReadOnlyDoubleWrapper simTime;
    private ReadOnlyDoubleWrapper carInput;
    private ReadOnlyDoubleWrapper rampInput;
    private List<VehicleSpawner> spawners;
    private List<VehicleCounter> counters;
    private int last = 0;
    @Getter
    private List<String> data;

    public DataSaver(SimulationController controller) {
        this.simTime = new ReadOnlyDoubleWrapper();
        this.simTime.bind(controller.getSimTotalSec());
        this.carInput = new ReadOnlyDoubleWrapper();
        this.carInput.bind(controller.getSpawnRateSlider().valueProperty());
        this.rampInput = new ReadOnlyDoubleWrapper();
        this.rampInput.bind(controller.getRampSpawnRateSlider().valueProperty());
        spawners = controller.getRoadObjectService().getSpawner();
        counters = controller.getRoadObjectService().getVehicleCounter();
        data = new ArrayList<>();
        simTime.addListener((observableValue, number, t1) -> {
            if (number.intValue() / SAVE_TIME > last) {
                save();
                last++;
            }
        });
    }



    public void save() {
        int vehicleSum = spawners.stream()
                .mapToInt(VehicleSpawner::getTotalSpawnedVehicles)
                .sum();
        StringBuilder sb = new StringBuilder();
        sb.append(simTime.intValue() + " ");
        sb.append(vehicleSum + " ");
        sb.append(carInput.getValue() + " ");
        sb.append(rampInput.getValue() + " ");
        counters.forEach(counter -> sb.append(counter.getThroughputPerHour() + " " + counter.getAvgSpeed() + " "));
        data.add(sb.toString().trim());
    }

    public void saveToFile(File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        PrintWriter writer = new PrintWriter(fileWriter);
        writer.println();
        data.forEach(writer::println);
        writer.close();
    }

    public void clearData(){
        data.clear();
        last = 0;
    }
}
