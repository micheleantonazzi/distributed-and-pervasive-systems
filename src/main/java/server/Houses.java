package server;

import messages.HouseMsgs.*;
import messages.StatisticMsgs.StatisticMsg;
import org.javatuples.Pair;

import java.util.*;

public class Houses {

    private Map<HouseInfoMsg, ArrayList<StatisticMsg>> houses = new HashMap<>();

    //Singleton
    private static Houses instance;

    private Houses(){}

    public static synchronized Houses getInstance(){
        if (instance == null)
            instance = new Houses();
        return instance;
    }

    public synchronized boolean add(HouseInfoMsg element){
        for (HouseInfoMsg house : this.houses.keySet()){
            if (house.getId() == element.getId())
                return false;
        }
        this.houses.put(element, new ArrayList<StatisticMsg>());
        return true;
    }

    public synchronized boolean addStatistic(HouseInfoMsg house, StatisticMsg statistic){
        if(this.houses.containsKey(house)){
            ArrayList<StatisticMsg> statistics = this.houses.get(house);

            // To order statistics, the last is in the end
            int i = statistics.size();
            while (i > 0 && statistics.get(i - 1).getTimestamp() > statistic.getTimestamp()){
                i--;
            }
            this.houses.get(house).add(i, statistic);
            return true;
        }
        return false;
    }

    public synchronized boolean remove(int id){
        for (HouseInfoMsg house : this.houses.keySet()){
            if (house.getId() == id){
                this.houses.remove(house);
                return true;
            }
        }
        return false;
    }

    public synchronized Set<HouseInfoMsg> getHouses() {
        return new HashSet<>(this.houses.keySet());
    }

    public synchronized boolean remove(HouseInfoMsg house){
        if(this.houses.containsKey(house)){
            this.houses.remove(house);
            return true;
        }
        return false;
    }

    public synchronized int size(){
        return this.houses.size();
    }

    public List<StatisticMsg> getStatistics(int id, int number){
        List<StatisticMsg> statistics = null;
        ArrayList<StatisticMsg> oldStatistics = new ArrayList<>();
        int limit = 0;

        synchronized (this){
            Set<HouseInfoMsg> keys = this.houses.keySet();
            for(Iterator<HouseInfoMsg> it = keys.iterator(); it.hasNext() && statistics == null;){
                HouseInfoMsg house = it.next();
                if(house.getId() == id){
                    statistics = new ArrayList<>();
                    // Save the arraylist because if the house exit it would be lost
                    oldStatistics = this.houses.get(house);
                    limit = oldStatistics.size() - 1;
                }
            }
        }

        if(statistics != null){
            for(int i = limit; i >= 0 && i > limit - number;--i)
                statistics.add(oldStatistics.get(i));
        }
        System.out.println(statistics);

        return statistics;
    }

    public Pair<Double, Double> getAverageAndDeviation(int id, int number){
        List<StatisticMsg> statistics = this.getStatistics(id, number);

        if(statistics == null)
            return null;

        Double average = statistics.stream().mapToDouble((statistic) -> statistic.getValue()).sum() / statistics.size();

        Double deviation = Math.sqrt((1.0 / statistics.size()) * statistics.stream().mapToDouble((statistic) -> Math.pow(statistic.getValue() - average, 2)).sum());

        return Pair.with(average, deviation);
    }
}
