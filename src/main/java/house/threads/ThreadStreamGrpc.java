package house.threads;

import house.services.HouseServicesGrpc;
import house.services.HouseServicesGrpc.HouseServicesStub;
import messages.HouseMsgs.HouseInfoMsg;

public abstract class ThreadStreamGrpc extends ThreadGrpc {

    private HouseServicesStub stub;

    public ThreadStreamGrpc(HouseInfoMsg destinationHouse){
        super(destinationHouse);

        this.stub = HouseServicesGrpc.newStub(super.getChannel());
    }

    public HouseServicesStub getStub(){
        return this.stub;
    }

}
