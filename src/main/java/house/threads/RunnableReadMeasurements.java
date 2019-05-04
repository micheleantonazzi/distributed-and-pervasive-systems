package house.threads;

import house.smartmeter.BufferSynchronized;
import house.smartmeter.Measurement;

import java.util.List;

public class RunnableReadMeasurements implements Runnable{

    @Override
    public void run() {
        while (true){
            List<Measurement> measurements = BufferSynchronized.getInstance().getMeasurements();
            System.out.println(measurements);
        }
    }
}
