package house.threads;

import house.services.HouseServicesGrpc;
import house.services.HouseServicesGrpc.HouseServicesBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import messages.HouseMsgs.HouseInfoMsg;

public abstract class RunnableGrpc implements Runnable {

    private ManagedChannel channel;
    private HouseServicesBlockingStub stub;
    private HouseInfoMsg destinationHouse;

    public RunnableGrpc(HouseInfoMsg destinationHouse){

        this.destinationHouse = destinationHouse;

        this.channel = ManagedChannelBuilder.forTarget(
                this.destinationHouse.getAddress() + ":" + this.destinationHouse.getPort()).usePlaintext(true).build();
        this.stub = HouseServicesGrpc.newBlockingStub(channel);
    }

    protected HouseInfoMsg getDestinationHouse(){
        return this.destinationHouse;
    }

    protected HouseServicesBlockingStub getStub(){
        return this.stub;
    }

    protected ManagedChannel getChannel(){
        return this.channel;
    }

}
