package house.threads;

import messages.HouseMsgs.HouseInfoMsg;
import utility.Houses;

public class RunnableSayHello extends RunnableGrpc{

    private HouseInfoMsg thisHouse;

    public RunnableSayHello(HouseInfoMsg thisHouse, HouseInfoMsg destinationHouse){
        super(destinationHouse);
        this.thisHouse = thisHouse;
    }

    @Override
    public void run() {
        super.getStub().hello(thisHouse);
        super.getChannel().shutdown();
    }
}
