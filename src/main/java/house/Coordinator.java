package house;

import messages.HouseMsgs.HouseInfoMsg;

import java.util.*;

public class Coordinator {

    private static Coordinator instance;

    private List<HouseInfoMsg> houses = new ArrayList<>();

    private boolean isCoordinator = false;

    private boolean communicationWithOldCoordinator = false;

    private ThreadBecomeCoordinator threadBecomeCoordinator;

    private Coordinator(){}

    public synchronized static Coordinator getInstance(){
        if(instance == null)
            instance = new Coordinator();
        return instance;
    }

    public synchronized boolean removeHouse(HouseInfoMsg house){
        if(!this.houses.remove(house))
            return false;

        this.canBecomeCoordinator();
        return true;
    }

    public synchronized boolean addHouse(HouseInfoMsg house){
        if(!this.houses.add(house))
            return false;

        this.canBecomeCoordinator();
        return true;
    }

    public synchronized void setHouses(List<HouseInfoMsg> houses){
        this.houses = new ArrayList<>(houses);
        //this.becomeCoordinator();
    }

    public synchronized boolean isCoordinator(){
        return this.isCoordinator;
    }



    // Private methods, used only by the inner thread

    private synchronized void becomeCoordinator(){
        this.isCoordinator = true;
        this.communicationWithOldCoordinator = false;
    }

    private synchronized void notCoordinator(){
        this.isCoordinator = false;
        this.communicationWithOldCoordinator = false;
        System.out.println("not coordinator");
    }

    // Not synchronized because is used by a single thread
    private synchronized void startCommunication(){
        this.communicationWithOldCoordinator = true;
    }

    // Not synchronized because is used by a single thread
    private void canBecomeCoordinator(){

        // if another thread is running stop and reinitialize it
        if(this.threadBecomeCoordinator != null) {
            try {
                threadBecomeCoordinator.join();
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }

        // Start new thread
        this.threadBecomeCoordinator = new ThreadBecomeCoordinator(new ArrayList<>(this.houses));
        this.threadBecomeCoordinator.start();

    }

    private class ThreadBecomeCoordinator extends Thread{

        private ArrayList<HouseInfoMsg> houses;

        public ThreadBecomeCoordinator(ArrayList<HouseInfoMsg> houses){
            this.houses = houses;
            HouseInfoMsg currentHouse = HouseMain.getHouseInfo();
            this.houses.remove(currentHouse);
        }

        private boolean highest(){
            HouseInfoMsg currentHouse = HouseMain.getHouseInfo();
            for(HouseInfoMsg house : this.houses){
                if(house.getId() > currentHouse.getId())
                    return false;
            }
            return true;
        }

        @Override
        public void run(){
            // I can become the coordinator
            if(this.highest()){
                if(this.houses.size() > 0){
                    this.houses.sort(Comparator.comparingInt(HouseInfoMsg::getId));
                    HouseInfoMsg oldCoordinator = this.houses.get(this.houses.size() - 1);

                    Coordinator.getInstance().startCommunication();
                    // Communication with to old coordinator

                }
                Coordinator.getInstance().becomeCoordinator();
            }
            else
                Coordinator.getInstance().notCoordinator();
        }
    }
}


