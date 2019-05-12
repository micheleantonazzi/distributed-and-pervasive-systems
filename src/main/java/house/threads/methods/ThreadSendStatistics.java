package house.threads.methods;

import house.HouseMain;
import house.services.HouseServicesOuterClass.Response;
import house.threads.ThreadStreamGrpc;
import io.grpc.stub.StreamObserver;
import messages.HouseMsgs.HouseInfoMsg;
import messages.StatisticMsgs;
import messages.StatisticMsgs.StatisticMsg;
import messages.StatisticMsgs.StatisticHouseMsg;
import utility.HousesAndStatistics;

public class ThreadSendStatistics extends ThreadStreamGrpc {

    private StatisticMsg statistic;

    private Object lock = new Object();

    private StreamObserver<StatisticMsgs.StatisticHouseMsg> sendStream;

    public ThreadSendStatistics(HouseInfoMsg destinationHouse){
        super(destinationHouse);
        this.sendStream = super.getStub().sendStatistic(new StreamObserver<Response>() {
            @Override
            public void onNext(Response response) {
                System.out.println("risposta");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("House " + destinationHouse.getId() + " is unexpectedly disconnected");
                HousesAndStatistics.getInstance().removeHouse(destinationHouse);
            }

            @Override
            public void onCompleted() {
                System.out.println("completed");
            }
        });
    }

    @Override
    public void run(){
        while (true){
            synchronized (this.lock){
                try {
                    this.lock.wait();
                    this.sendStream.onNext(
                            StatisticHouseMsg.newBuilder()
                                    .setHouseInfo(HouseMain.getHouseInfo()).setStatistic(this.statistic).build()
                    );
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
        }
    }

    public void sendStatistic(StatisticMsg statistic){
        this.statistic = statistic;
        synchronized (this.lock){
            this.lock.notify();
        }
    }

    public void stopAndClose(){
        this.sendStream.onCompleted();
        this.getChannel().shutdown();
        this.stop();
    }
}
