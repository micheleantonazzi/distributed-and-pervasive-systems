package utility;

import house.HouseMain;
import house.threads.grpc.methods.ThreadSendStatistics;
import messages.HouseMsgs.HouseInfoMsg;
import messages.StatisticMsgs.StatisticMsg;
import org.javatuples.Pair;

import java.util.*;
import java.util.stream.Collectors;


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
        return new HashSet<>(this.housesMap.keySet());
    }

    public Set<HouseInfoMsg> getOtherHouses(){
        Set<HouseInfoMsg> houses;
        synchronized (this){
            houses = new HashSet<>(this.housesMap.keySet());
        }

        houses.remove(HouseMain.getHouseInfo());
        return houses;
    }


    public synchronized void addHouse(HouseInfoMsg house){
        this.housesMap.put(house, Pair.with(null, new ArrayList<>()));
    }

    public synchronized void removeHouse(HouseInfoMsg house){
        Pair<ThreadSendStatistics, ArrayList<StatisticMsg>> pair = this.housesMap.get(house);
        if(pair != null && pair.getValue0() != null)
            pair.getValue0().stopAndClose();

        // To get up ThreadGlobalStatistic
        notify();

        this.housesMap.remove(house);
    }

    public synchronized boolean addStatistic(HouseInfoMsg house, StatisticMsg statistic){
        Pair<ThreadSendStatistics, ArrayList<StatisticMsg>> pair = this.housesMap.get(house);
        if(pair == null){
            return false;
        }
        pair.getValue1().add(statistic);

        // To get up ThreadGlobalStatistic
        notify();
        return true;
    }

    public Set<ThreadSendStatistics> getThreadsSendStatistics(){
        HouseInfoMsg currentHouse = HouseMain.getHouseInfo();
        HashMap<HouseInfoMsg, Pair<ThreadSendStatistics, ArrayList<StatisticMsg>>> housesMapClone;
        Set<ThreadSendStatistics> ret = new HashSet<>();

        synchronized (this){
           housesMapClone = new HashMap<>(this.housesMap);
        }

        housesMapClone.remove(currentHouse);

        for(HouseInfoMsg house : housesMapClone.keySet()){

            Pair<ThreadSendStatistics, ArrayList<StatisticMsg>> pair = housesMapClone.get(house);
            ThreadSendStatistics thread = pair.getValue0();

            // Create thread if it's null
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

    public synchronized void stopAll(){
        for(HouseInfoMsg house : this.housesMap.keySet()){
            if(this.housesMap.get(house).getValue0() != null)
                this.housesMap.get(house).getValue0().stopAndClose();
        }
    }

    private boolean allHaveStatistics(){
        List<ArrayList<StatisticMsg>> allStatistics = this.housesMap.values()
                .stream().map(pair -> pair.getValue1()).collect(Collectors.toList());
        for(int i = 0; i < allStatistics.size(); ++i){
            if (allStatistics.get(i).size() == 0)
                return false;
        }
        return true;
    }

    public List<StatisticMsg> getAllStatistics(){
        List<ArrayList<StatisticMsg>> allStatistics;

        synchronized (this){

            while(!this.allHaveStatistics()) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }

            allStatistics = this.housesMap.values()
                    .stream().map(pair -> pair.getValue1()).collect(Collectors.toList());

            // Delete old statistics
            for(HouseInfoMsg house : this.housesMap.keySet())
                this.housesMap.put(house,
                        Pair.with(this.housesMap.get(house).getValue0(), new ArrayList<>()));
        }

        return allStatistics.stream().map((statistics)->{
            long timestamp = statistics.get(statistics.size() - 1).getTimestamp();
            int size = statistics.size();
            return StatisticMsg.newBuilder()
                    .setValue(statistics.stream().mapToDouble((statistic) -> statistic.getValue()).sum() / size)
                    .setTimestamp(timestamp).build();
        }).collect(Collectors.toList());
    }
}
