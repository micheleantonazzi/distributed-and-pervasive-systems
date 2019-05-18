package server;

import messages.StatisticMsgs.StatisticMsg;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class GlobalStatistics {

    private static GlobalStatistics instance;

    private ArrayList<StatisticMsg> statistics = new ArrayList<>();

    private GlobalStatistics(){}

    public synchronized static GlobalStatistics getInstance(){
        if(instance == null)
            instance = new GlobalStatistics();

        return instance;
    }

    public synchronized void add(StatisticMsg statistic){
        int i = statistics.size();
        while (i > 0 && statistics.get(i - 1).getTimestamp() > statistic.getTimestamp()){
            i--;
        }
        this.statistics.add(i, statistic);
    }

    public List<StatisticMsg> getLasts(int number){
        int limit;
        synchronized (this){
            limit = this.statistics.size() - 1;
        }

        ArrayList<StatisticMsg> ret = new ArrayList<>();

        for(int i = limit; i >= 0 && i > limit - number; --i)
            ret.add(this.statistics.get(i));

        return ret;
    }

    public Pair<Double, Double> getAverageAndDeviation(int number){
        List<StatisticMsg> statistics = this.getLasts(number);

        if(statistics.size() == 0)
            return Pair.with(0.0, 0.0);

        Double average = statistics.stream().mapToDouble((statistic) -> statistic.getValue()).sum() / statistics.size();

        Double deviation = Math.sqrt((1.0 / statistics.size()) * statistics.stream().mapToDouble((statistic) -> Math.pow(statistic.getValue() - average, 2)).sum());

        return Pair.with(average, deviation);
    }
}
