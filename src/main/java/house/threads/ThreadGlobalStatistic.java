package house.threads;

import house.Coordinator;
import messages.StatisticMsgs.StatisticMsg;
import house.HousesAndStatistics;
import server.ServerMain;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.List;

public class ThreadGlobalStatistic extends Thread {

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(ServerMain.SERVER_URI);

    @Override
    public void run(){
        while(true){
            List<StatisticMsg> statistics = HousesAndStatistics.getInstance().getAllStatistics();
            StatisticMsg globalStatistic = StatisticMsg.newBuilder()
                    .setValue(statistics.stream().mapToDouble(statistic -> statistic.getValue()).sum())
                    .setTimestamp(statistics.stream().mapToLong(statistic -> statistic.getTimestamp()).max().getAsLong())
                    .build();

            System.out.println("Global Statistic of " + statistics.size() + " houses:\n" +
                    globalStatistic.getTimestamp() + " -> " + globalStatistic.getValue());

            if(Coordinator.getInstance().isCoordinator()){
                System.out.println("I'm the coordinator and send global statistic\n");
                new Thread(new RunnableSendGlobalStatistic(globalStatistic, target)).start();
            }
            else
                System.out.println();
        }
    }

    public void stopAndClose(){
        this.client.close();
        this.stop();
    }
}
