package house.threads.grpc;

import house.services.HouseServicesGrpc;
import house.services.HouseServicesGrpc.HouseServicesBlockingStub;
import messages.HouseMsgs.HouseInfoMsg;

public abstract class ThreadUnaryGrpc extends ThreadGrpc {

    private HouseServicesBlockingStub stub;

    public ThreadUnaryGrpc(HouseInfoMsg destinationHouse){
        super(destinationHouse);

        this.stub = HouseServicesGrpc.newBlockingStub(super.getChannel());
    }

    protected HouseServicesBlockingStub getStub(){
        return this.stub;
    }

}
