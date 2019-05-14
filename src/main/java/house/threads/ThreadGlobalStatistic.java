package house.threads;

import utility.HousesAndStatistics;

public class ThreadGlobalStatistic extends Thread {

    @Override
    public void run(){
        while(true){
            System.out.println("Calcolo la statistica con " + HousesAndStatistics.getInstance().getAllStatistics().size());
        }
    }


}
