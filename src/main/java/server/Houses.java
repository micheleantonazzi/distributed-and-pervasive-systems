package server;

import messages.HouseMsgs.*;
import utility.HashSetSynchronized;
import java.util.Set;

public class Houses extends HashSetSynchronized<HouseInfoMsg> {

    //Singleton
    private static Houses instance;

    private Houses(){}

    public static Houses getInstance(){
        if (instance == null)
            instance = new Houses();
        return instance;
    }

    @Override
    public synchronized boolean add(HouseInfoMsg element){
        Set<HouseInfoMsg> houses = super.getSet();
        for (HouseInfoMsg house : houses){
            if (house.getId() == element.getId())
                return false;
        }
        return super.add(element);
    }

    public synchronized boolean removeHouseFromId(int id){
        Set<HouseInfoMsg> houses = super.getSet();
        System.out.println(houses);
        for (HouseInfoMsg house : houses){
            if (house.getId() == id)
                return super.remove(house);
        }
        return false;
    }

}
