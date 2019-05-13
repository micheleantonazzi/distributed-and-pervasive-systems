package house.threads.grpc.methods;

import house.HouseMain;
import house.services.HouseServicesOuterClass.Response;
import house.threads.grpc.ThreadStreamGrpc;
import io.grpc.stub.StreamObserver;
import messages.HouseMsgs.HouseInfoMsg;
import messages.StatisticMsgs;
import messages.StatisticMsgs.StatisticMsg;
import messages.StatisticMsgs.StatisticHouseMsg;
import server.ServerMain;
import utility.HousesAndStatistics;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class ThreadSendStatistics extends ThreadStreamGrpc {

    private StatisticMsg statistic;

    private Object lock = new Object();

    private StreamObserver<StatisticMsgs.StatisticHouseMsg> sendStream;

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(ServerMain.SERVER_URI);

    public ThreadSendStatistics(HouseInfoMsg destinationHouse){
        super(destinationHouse);
        this.sendStream = super.getStub().sendStatistic(new StreamObserver<Response>() {
            @Override
            public void onNext(Response response) {

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

                    // Send asynchronously statistic to server
                    new Thread(()->{
                        try{
                            target.path("house/sendstatistic").request().post(
                                    Entity.entity(
                                            StatisticHouseMsg.newBuilder()
                                                    .setHouseInfo(HouseMain.getHouseInfo())
                                                    .setStatistic(statistic).build().toByteArray(),
                                            MediaType.APPLICATION_OCTET_STREAM));
                        }
                        catch (ProcessingException ex){
                            Throwable cause = ex.getCause();
                            System.out.println(ex);
                            System.out.println(cause);
                            if (cause.getClass().getName().equals("java.net.ConnectException"))
                                //if the connection doesn't exist maybe the server isn't running
                                System.out.println("ERROR, the server couldn't be contacted, please check if it running.");
                            else
                                System.out.println(ex);
                        }
                    }).start();

                    // Send statistic to other houses
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
        this.client.close();
        this.sendStream.onCompleted();
        this.getChannel().shutdown();
        this.stop();
    }
}
