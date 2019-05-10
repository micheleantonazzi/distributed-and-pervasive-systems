package utility;

import messages.HouseMsgs.HouseInfoMsg;
import messages.StatisticMsgs.StatisticMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class HousesAndStatistics {

    private static HousesAndStatistics instance;

    private HashMap<HouseInfoMsg, ArrayList<StatisticMsg>> housesMap = new HashMap<>();

    public synchronized static HousesAndStatistics getInstance(){
        if (instance == null)
            instance = new HousesAndStatistics();
        return instance;
    }

    private HousesAndStatistics(){}

    public synchronized void setHouses(List<HouseInfoMsg> houses){
        this.housesMap = new HashMap<>();
        for (HouseInfoMsg house : houses)
            housesMap.put(house, new ArrayList<>());
    }

    public synchronized Set<HouseInfoMsg> getHouses(){
        return this.housesMap.keySet();
    }

    public synchronized void addHouse(HouseInfoMsg house){
        this.housesMap.put(house, new ArrayList<>());
    }

    public synchronized void removeHouse(HouseInfoMsg house){
        this.housesMap.remove(house);
    }

    public synchronized boolean addStatistic(HouseInfoMsg house, StatisticMsg statistic){
        ArrayList<StatisticMsg> statistics = this.housesMap.get(house);
        if(statistic == null){
            return false;
        }
        statistics.add(statistic);
        return true;
    }
}
