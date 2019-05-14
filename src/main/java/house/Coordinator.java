package house;

import messages.HouseMsgs.HouseInfoMsg;

import java.util.*;

public class Coordinator {

    private static Coordinator instance;

    private List<HouseInfoMsg> houses = new ArrayList<>();

    private boolean isCoordinator = false;

    private boolean communicationWithOldCoordinator = false;

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

    private boolean highest(){
        return true;
    }

    // When this method ends this house has to become the coordinator
    // It returns the old coordinator
    public HouseInfoMsg becomeCoordinator(){
        ArrayList<HouseInfoMsg> oldHouses;
        synchronized (this){
            while (this.isCoordinator || !highest()) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
            oldHouses = new ArrayList<>(this.houses);
            this.communicationWithOldCoordinator = true;
        }

        oldHouses.remove(HouseMain.getHouseInfo());
        oldHouses.sort(Comparator.comparingInt(HouseInfoMsg::getId));
        return oldHouses.get(0);
    }
}
