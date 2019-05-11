package utility;

import house.HouseMain;
import house.threads.methods.ThreadSendStatistics;
import messages.HouseMsgs.HouseInfoMsg;
import messages.StatisticMsgs.StatisticMsg;
import org.javatuples.Pair;

import java.util.*;


// This class contains the houses with the relative statistics and threads used to send statistic
public class HousesAndStatistics {

    private static HousesAndStatistics instance;

    private HashMap<HouseInfoMsg, Pair<ThreadSendStatistics, ArrayList<StatisticMsg>>> housesMap = new HashMap<>();

    public synchronized static HousesAndStatistics getInstance(){
        if (instance == null)
            instance = new HousesAndStatistics();
        return instance;
    }

    private HousesAndStatistics(){}

    public synchronized void setHouses(List<HouseInfoMsg> houses){
        HouseInfoMsg currentHouse = HouseMain.getHouseInfo();
        this.housesMap = new HashMap<>();
        for (HouseInfoMsg house : houses){
            ThreadSendStatistics threadSendStatistics = null;

            if(house != currentHouse){
                threadSendStatistics = new ThreadSendStatistics(house);
                threadSendStatistics.start();
            }

            housesMap.put(house, Pair.with(threadSendStatistics , new ArrayList<>()));
        }

    }

    public synchronized Set<HouseInfoMsg> getHouses(){
        return this.housesMap.keySet();
    }

    public Set<HouseInfoMsg> getOtherHouses(){
        Set<HouseInfoMsg> houses;
        synchronized (this){
            houses = this.housesMap.keySet();
        }

        houses.remove(HouseMain.getHouseInfo());
        return houses;
    }


    public synchronized void addHouse(HouseInfoMsg house){
        this.housesMap.put(house, Pair.with(null, new ArrayList<>()));
    }

    public synchronized void removeHouse(HouseInfoMsg house){
        Pair<ThreadSendStatistics, ArrayList<StatisticMsg>> pair = this.housesMap.get(house);
        if(pair.getValue0() != null)
            pair.getValue0().stopAndClose();
        this.housesMap.remove(house);
    }

    public synchronized boolean addStatistic(HouseInfoMsg house, StatisticMsg statistic){
        Pair<ThreadSendStatistics, ArrayList<StatisticMsg>> pair = this.housesMap.get(house);
        if(pair == null){
            return false;
        }
        pair.getValue1().add(statistic);
        return true;
    }

    public Set<ThreadSendStatistics> getThreadsSendStatistics(){
        HouseInfoMsg currentHouse = HouseMain.getHouseInfo();
        HashMap<HouseInfoMsg, Pair<ThreadSendStatistics, ArrayList<StatisticMsg>>> housesMapClone;
        Set<ThreadSendStatistics> ret = new HashSet<>();

        synchronized (this){
           housesMapClone = new HashMap<>(this.housesMap);
        }

        housesMapClone.remove(this.housesMap.get(currentHouse));

        //System.out.println(housesMapClone);

        for(HouseInfoMsg house : housesMapClone.keySet()){

            Pair<ThreadSendStatistics, ArrayList<StatisticMsg>> pair = housesMapClone.get(house);
            ThreadSendStatistics thread = pair.getValue0();

            if(thread == null){
                synchronized (this){
                    thread = new ThreadSendStatistics(house);
                    thread.start();
                    this.housesMap.put(
                            house, this.housesMap.get(house).setAt0(thread));
                }
            }
            ret.add(thread);
        }

        return ret;
    }
}
