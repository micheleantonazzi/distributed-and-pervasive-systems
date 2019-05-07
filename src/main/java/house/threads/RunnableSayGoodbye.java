package house.threads;

import messages.HouseMsgs.HouseInfoMsg;

public class RunnableSayGoodbye extends RunnableGrpc {

    private HouseInfoMsg thisHouse;

    public RunnableSayGoodbye(HouseInfoMsg thisHouse, HouseInfoMsg destinationHouse){
        super(destinationHouse);
        this.thisHouse = thisHouse;
    }

    @Override
    public void run() {
        super.getStub().goodbye(thisHouse);
        super.getChannel().shutdown();
    }
}
