package house.threads.grpc.methods;

import house.HouseMain;
import house.services.HouseServicesOuterClass.Response;
import house.threads.grpc.ThreadStreamGrpc;
import io.grpc.stub.StreamObserver;
import messages.HouseMsgs.HouseInfoMsg;
import messages.StatisticMsgs.StatisticMsg;
import messages.StatisticMsgs.StatisticHouseMsg;
import house.HousesAndStatistics;
import server.ServerMain;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;


public class ThreadSendStatistics extends ThreadStreamGrpc {

    private StatisticMsg statistic;

    private Object lock = new Object();

    private StreamObserver<StatisticHouseMsg> sendStream;

    public ThreadSendStatistics(HouseInfoMsg destinationHouse){
        super(destinationHouse);
        this.sendStream = super.getStub().sendStatistic(new StreamObserver<Response>() {
            @Override
            public void onNext(Response response) {

            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("House " + destinationHouse.getId() + " is unexpectedly disconnected\n");

                // Delete house localy
                HousesAndStatistics.getInstance().removeHouse(destinationHouse);

                // Delete house remotely
                Client client = ClientBuilder.newClient();
                WebTarget target = client.target(ServerMain.SERVER_URI);
                try{
                    target.path("house/unexpectedexit/" + destinationHouse.getId())
                            .request().delete();
                }
                catch (ProcessingException e){
                    if(e.getClass().getName().equals("java.net.ConnectException"))
                        System.out.println("ERROR, the server couldn't be contacted, please check if it running.");
                    else
                        System.out.println(e);
                }
                finally {
                    client.close();
                }
            }

            @Override
            public void onCompleted() {
            }
        });
    }

    @Override
    public void run(){
        while (true){
            synchronized (this.lock){
                try {
                    this.lock.wait();

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
        this.sendStream.onCompleted();
        this.getChannel().shutdown();
        this.stop();
    }
}
