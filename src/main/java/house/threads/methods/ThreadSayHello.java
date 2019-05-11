package house.threads.methods;

import house.threads.ThreadUnaryGrpc;
import messages.HouseMsgs.HouseInfoMsg;

public class ThreadSayHello extends ThreadUnaryGrpc {

    private HouseInfoMsg thisHouse;

    public ThreadSayHello(HouseInfoMsg thisHouse, HouseInfoMsg destinationHouse){
        super(destinationHouse);
        this.thisHouse = thisHouse;
    }

    @Override
    public void run() {
        super.getStub().hello(thisHouse);
        //super.getChannel().shutdown();
    }
}
