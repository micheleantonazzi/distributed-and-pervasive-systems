package utility;

import messages.HouseMsgs.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Houses {

    private Set<HouseInfoMsg> houses = new HashSet<>();

    //Singleton
    private static Houses instance;

    private Houses(){}

    public static Houses getInstance(){
        if (instance == null)
            instance = new Houses();
        return instance;
    }

    public synchronized boolean add(HouseInfoMsg element){
        for (HouseInfoMsg house : this.houses){
            if (house.getId() == element.getId())
                return false;
        }
        return this.houses.add(element);
    }

    public synchronized boolean removeHouseFromId(int id){
        for (HouseInfoMsg house : this.houses){
            if (house.getId() == id)
                return this.houses.remove(house);
        }
        return false;
    }

    public synchronized void setHouses(List<HouseInfoMsg> list){
        for(HouseInfoMsg house : list)
            this.houses.add(house);
    }

    public synchronized Set<HouseInfoMsg> getSet() {

        Set<HouseInfoMsg> ret = new HashSet<>(this.houses);
        return ret;
    }

    public synchronized boolean remove(HouseInfoMsg house){
        return this.houses.remove(house);
    }

    public synchronized int size(){
        return this.houses.size();
    }
}
