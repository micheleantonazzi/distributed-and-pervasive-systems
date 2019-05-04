package house.threads;

import house.smartmeter.BufferSynchronized;
import house.smartmeter.Measurement;

import java.util.List;

public class ThreadReadMeasurements extends Thread{

    private volatile boolean stop = false;

    @Override
    public void run() {
        while (!this.stop){
            List<Measurement> measurements = BufferSynchronized.getInstance().getMeasurements();
            System.out.println(measurements);
        }
    }

    public void stopMeGently(){
        this.stop = true;
        BufferSynchronized.getInstance().notifyAll();
    }
}
