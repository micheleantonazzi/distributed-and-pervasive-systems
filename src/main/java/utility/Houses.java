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
        this.houses.put(element, new ArrayList<>());
        return true;
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

    public synchronized void setHouses(List<HouseInfoMsg> list){
        for(HouseInfoMsg house : list)
            this.houses.put(house, new ArrayList<>());
    }

    public synchronized Set<HouseInfoMsg> getHouses() {

        return this.houses.keySet();
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
