package house;

import messages.HouseMsgs.HouseInfoMsg;

import java.util.HashSet;
import java.util.Set;

public class Coordinator {

    private static Coordinator instance;

    private Set<HouseInfoMsg> houses = new HashSet<>();

    private Coordinator(){}

    public synchronized static Coordinator getInstance(){
        if(instance == null)
            instance = new Coordinator();
        return instance;
    }

    public synchronized boolean removeHouse(HouseInfoMsg house){
        if(!this.houses.remove(house))
            return false;

        notify();
        return false;
    }

    public synchronized boolean addHouse(HouseInfoMsg house){
        if(!this.houses.add(house))
            return false;

        notify();
        return false;
    }
}
