package utility;

import messages.HouseMsgs.*;
import messages.StatisticMsgs.StatisticMsg;

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

            // To order statistics, the last is in first position
            int i = 0;
            while (i < statistics.size() && statistics.get(i).getTimestamp() > statistic.getTimestamp()){
                i++;
            }
            this.houses.get(house).add(i, statistic);
            return true;
        }
        return false;
    }

    public synchronized boolean removeHouseFromId(int id){
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
}
