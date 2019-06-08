package house.threads.grpc.methods;

import house.threads.grpc.ThreadUnaryGrpc;
import messages.HouseMsgs.HouseInfoMsg;

public class ThreadSayGoodbye extends ThreadUnaryGrpc {

    private HouseInfoMsg thisHouse;

    public ThreadSayGoodbye(HouseInfoMsg thisHouse, HouseInfoMsg destinationHouse){
        super(destinationHouse);
        this.thisHouse = thisHouse;
    }

    @Override
    public void run() {
        super.getStub().goodbye(thisHouse);
        super.getChannel().shutdown();
    }
}
