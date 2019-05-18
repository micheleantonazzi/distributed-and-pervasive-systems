package house.smartmeter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BufferSynchronized implements Buffer {

    private LinkedList<Measurement> queue = new LinkedList<>();

    private static BufferSynchronized instance;

    private BufferSynchronized(){}

    public static synchronized BufferSynchronized getInstance(){
        if(instance == null)
            instance = new BufferSynchronized();
        return instance;
    }

    @Override
    public void addMeasurement(Measurement measurement){
        this.queue.add(measurement);

        synchronized (this){
            if(this.queue.size() >= 24)
                notify();
        }
    }

    // This type of synchronize works correctly only if there is a single thread that reads the measurements
    public List<Measurement> getMeasurements(){
        synchronized (this){
            if(this.queue.size() < 24) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                    return new ArrayList<>();
                }
            }
        }

        List<Measurement> ret = new ArrayList<>();
        for(int i = 0; i < 12; ++i)
            ret.add(queue.remove());
        for(int i = 0 ; i < 12; ++i)
            ret.add(queue.get(i));
        return ret;
    }
}
