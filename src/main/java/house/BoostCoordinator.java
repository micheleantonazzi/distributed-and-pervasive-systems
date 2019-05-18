package house;

import house.services.HouseServicesGrpc;
import house.services.HouseServicesGrpc.HouseServicesStub;
import house.services.HouseServicesOuterClass.Response;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import messages.HouseMsgs.HouseInfoMsg;

import java.util.HashMap;
import java.util.Map;

public class BoostCoordinator {

    private static BoostCoordinator instance;

    public enum Status{FREE, WAIT, BOOST}

    private Status status = Status.FREE;

    private Map<HouseInfoMsg, StreamObserver<Response>> houses = new HashMap<>();

    private Map<HouseInfoMsg, StreamObserver<Response>> notifyHouses = new HashMap<>();

    private ThreadTryBoost threadTryBoost;

    private Thread threadBoost;

    private BoostCoordinator(){}

    public static BoostCoordinator getInstance(){
        if(instance == null)
            instance = new BoostCoordinator();
        return instance;
    }

    public synchronized boolean tryAcquireBoost(){

        if(this.status != Status.FREE)
            return false;

        this.houses = new HashMap<>();
        this.notifyHouses = new HashMap<>();

        for(HouseInfoMsg house : HousesAndStatistics.getInstance().getOtherHouses()){

            final ManagedChannel channel = ManagedChannelBuilder
                    .forAddress(house.getAddress(), house.getPort()).usePlaintext(true).build();

            HouseServicesStub stub = HouseServicesGrpc.newStub(channel);

            StreamObserver<Response> streamObserver = new StreamObserver<Response>() {
                @Override
                public void onNext(Response response) {}

                @Override
                public void onError(Throwable throwable) {
                    channel.shutdown();
                }

                @Override
                public void onCompleted() {
                    BoostCoordinator.getInstance().removeHouse(house);
                    channel.shutdown();
                }
            };

            this.houses.put(house, streamObserver);

            stub.acquireBoost(HouseMain.getHouseInfo(), streamObserver);
        }

        this.status = Status.WAIT;
        this.threadTryBoost = new ThreadTryBoost();
        this.threadTryBoost.start();

        return true;
    }

    public synchronized void removeHouse(HouseInfoMsg house){
        if(this.houses.containsKey(house)){
            this.houses.remove(house);
            notify();
        }
    }

    public synchronized void request(HouseInfoMsg house, StreamObserver<Response> streamObserver){

        if(this.status == Status.FREE){
            streamObserver.onNext(Response.newBuilder().setStatus(Response.Status.OK).build());
            streamObserver.onCompleted();
        }
        else if(this.status == Status.BOOST){
            streamObserver.onNext(Response.newBuilder().setStatus(Response.Status.BOOST).build());
            this.notifyHouses.put(house, streamObserver);
        }
        else{

            // If the house isn't in the list or its id is smaller than mine, put it in wait
            if(!this.houses.containsKey(house) || HouseMain.getHouseInfo().getId() > house.getId()){
                streamObserver.onNext(Response.newBuilder().setStatus(Response.Status.WAIT).build());
                this.notifyHouses.put(house, streamObserver);
            }
            else {
                streamObserver.onNext(Response.newBuilder().setStatus(Response.Status.OK).build());
                streamObserver.onCompleted();
            }
        }
    }

    private synchronized void boost(){
        while (this.houses.size() >= 2) {
            try {
                wait();
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }

        this.status = Status.BOOST;

        System.out.println("BOOST");


        // BOOST
        this.threadBoost = new Thread(()->{
            try {
                HouseMain.getSmartMeter().boost();
            } catch (InterruptedException ex) {
            }
            BoostCoordinator.getInstance().stopBoost();

        });
        this.threadBoost.start();
    }

    private synchronized void stopBoost(){
        System.out.println("BOOST END");

        this.status = Status.FREE;

        for(HouseInfoMsg house : this.notifyHouses.keySet()){
            try{
                this.notifyHouses.get(house).onNext(Response.newBuilder().setStatus(Response.Status.OK).build());
                this.notifyHouses.get(house).onCompleted();
            }
            catch (StatusRuntimeException ex){ }
        }
    }

    public void stopAndClose(){
        if(this.status != Status.FREE){
            this.threadTryBoost.stop();

            if(this.threadBoost != null)
                this.threadBoost.stop();

            for(HouseInfoMsg house : this.notifyHouses.keySet()){
                try{
                    this.notifyHouses.get(house).onNext(Response.newBuilder().setStatus(Response.Status.OK).build());
                    this.notifyHouses.get(house).onCompleted();
                }
                catch (StatusRuntimeException ex){}
            }
        }

    }


    private class ThreadTryBoost extends Thread{

        @Override
        public void run(){
            BoostCoordinator.getInstance().boost();
        }
    }
}
