package house.threads;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import messages.HouseMsgs.HouseInfoMsg;

public abstract class ThreadGrpc extends Thread {

    private ManagedChannel channel;
    private HouseInfoMsg destinationHouse;

    public ThreadGrpc(HouseInfoMsg destinationHouse){

        this.destinationHouse = destinationHouse;

        this.channel = ManagedChannelBuilder.forTarget(
                this.destinationHouse.getAddress() + ":" + this.destinationHouse.getPort()).usePlaintext(true).build();
    }

    public HouseInfoMsg getDestinationHouse(){
        return this.destinationHouse;
    }

    public ManagedChannel getChannel(){
        return this.channel;
    }

    @Override
    public abstract void run();

}
