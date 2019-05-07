package house.threads;

import house.smartmeter.BufferSynchronized;
import house.smartmeter.Measurement;

import java.util.List;

public class ThreadReadMeasurements extends Thread{

    @Override
    public void run() {
        while (true){
            List<Measurement> measurements = BufferSynchronized.getInstance().getMeasurements();
            double average = measurements.stream().mapToDouble(measurement -> measurement.getValue()).sum() / measurements.size();
            System.out.println(average);
        }
    }
}
