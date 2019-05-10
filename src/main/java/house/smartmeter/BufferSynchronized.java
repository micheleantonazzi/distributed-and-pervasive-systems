package house.smartmeter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BufferSynchronized implements Buffer {

    private Queue<Measurement> queue = new LinkedList<>();

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

    public List<Measurement> getMeasurements(){
        synchronized (this){
            if(this.queue.size() < 24) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.out.println(e);
                    return new ArrayList<>();
                }
            }
        }

        List<Measurement> ret = new ArrayList<>();
        for(int i = 0; i < 12; ++i)
            ret.add(queue.remove());
        return ret;
    }
}
