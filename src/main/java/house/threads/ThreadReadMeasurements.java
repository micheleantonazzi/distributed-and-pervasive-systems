package house.threads;

import house.HouseMain;
import house.smartmeter.BufferSynchronized;
import house.smartmeter.Measurement;
import messages.StatisticMsgs.StatisticMsg;
import messages.StatisticMsgs.StatisticHouseMsg;
import utility.HousesAndStatistics;

import java.util.List;

public class ThreadReadMeasurements extends Thread{

    @Override
    public void run() {
        while (true){
            List<Measurement> measurements = BufferSynchronized.getInstance().getMeasurements();
            double average = measurements.stream().mapToDouble(measurement -> measurement.getValue()).sum() / measurements.size();

            StatisticMsg statistic = StatisticMsg.newBuilder().setValue(average).setTimestamp(measurements.get(measurements.size() - 1).getTimestamp()).build();

            // Add statistic about this house
            HousesAndStatistics.getInstance().addStatistic(HouseMain.getHouseInfo(), statistic);

        }
    }
}
