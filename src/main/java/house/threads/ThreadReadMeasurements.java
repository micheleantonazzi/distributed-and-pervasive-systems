package house.threads;

import house.HouseMain;
import house.smartmeter.BufferSynchronized;
import house.smartmeter.Measurement;
import house.threads.grpc.methods.ThreadSendStatistics;
import messages.StatisticMsgs;
import messages.StatisticMsgs.StatisticMsg;
import server.ServerMain;
import house.HousesAndStatistics;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.List;

public class ThreadReadMeasurements extends Thread{

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(ServerMain.SERVER_URI);


    @Override
    public void run() {
        while (true){
            List<Measurement> measurements = BufferSynchronized.getInstance().getMeasurements();
            double average = measurements.stream().mapToDouble(measurement -> measurement.getValue()).sum() / measurements.size();

            StatisticMsg statistic = StatisticMsg.newBuilder().setValue(average).setTimestamp(measurements.get(measurements.size() - 1).getTimestamp()).build();

            // Add statistic about this house
            HousesAndStatistics.getInstance().addStatistic(HouseMain.getHouseInfo(), statistic);

            // Send asynchronously statistic to server
            new Thread(
                    new RunnableSendStatistic(
                            StatisticMsgs.StatisticHouseMsg.newBuilder()
                            .setHouseInfo(HouseMain.getHouseInfo())
                            .setStatistic(statistic).build(), target
                    )
            ).start();

            // Send statistic to other houses
            for(ThreadSendStatistics thread : HousesAndStatistics.getInstance().getThreadsSendStatistics())
                thread.sendStatistic(statistic);
        }
    }

    public void stopAndClose(){
        this.client.close();
        this.stop();
    }
}
