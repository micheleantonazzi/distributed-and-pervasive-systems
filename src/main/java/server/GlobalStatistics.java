package server;

import messages.StatisticMsgs.StatisticMsg;

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
}
