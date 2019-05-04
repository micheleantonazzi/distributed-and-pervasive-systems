package house.threads;

import house.services.HouseServicesGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import messages.HouseMsgs.HouseInfoMsg;
import utility.Houses;

public class RunnableSayHello implements Runnable{

    private HouseInfoMsg thisHouse;
    private HouseInfoMsg destinationHouse;


    public RunnableSayHello(HouseInfoMsg thisHouse, HouseInfoMsg destinationHouse){
        this.thisHouse = thisHouse;
        this.destinationHouse = destinationHouse;
    }

    @Override
    public void run() {
        try{
            ManagedChannel channel = ManagedChannelBuilder.forTarget(
                    destinationHouse.getAddress() + ":" + destinationHouse.getPort()).usePlaintext(true).build();
            HouseServicesGrpc.HouseServicesBlockingStub stub = HouseServicesGrpc.newBlockingStub(channel);
            stub.hello(thisHouse);
        }
        catch (io.grpc.StatusRuntimeException ex){
            Throwable cause = ex.getCause().getCause();

            //The house isn't online
            if(cause instanceof java.net.ConnectException){
                System.out.println("House " + destinationHouse.getId() + " is unexpectedly disconnected.");

                //Remove house locally
                Houses.getInstance().removeHouseFromId(destinationHouse.getId());
            }
        }
    }
}
