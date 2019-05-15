package house.threads;

import house.Coordinator;
import messages.StatisticMsgs.StatisticMsg;
import house.HousesAndStatistics;

import java.util.List;

public class ThreadGlobalStatistic extends Thread {

    @Override
    public void run(){
        while(true){
            List<StatisticMsg> statistics = HousesAndStatistics.getInstance().getAllStatistics();
            StatisticMsg globalStatistic = StatisticMsg.newBuilder()
                    .setValue(statistics.stream().mapToDouble(statistic -> statistic.getValue()).sum())
                    .setTimestamp(statistics.stream().mapToLong(statistic -> statistic.getTimestamp()).max().getAsLong())
                    .build();

            System.out.println("Global Statistic of " + statistics.size() + " houses:\n" + globalStatistic);

            if(Coordinator.getInstance().isCoordinator()){
                System.out.println("I'm the coordinator and send global statistic");
            }
        }
    }


}
