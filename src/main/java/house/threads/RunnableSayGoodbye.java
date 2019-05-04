package house.threads;

import messages.HouseMsgs.HouseInfoMsg;
import utility.Houses;

public class RunnableSayGoodbye extends RunnableGrpc {

    private HouseInfoMsg thisHouse;

    public RunnableSayGoodbye(HouseInfoMsg thisHouse, HouseInfoMsg destinationHouse){
        super(destinationHouse);
        this.thisHouse = thisHouse;
    }

    @Override
    public void run() {
        try{
            super.getStub().goodbye(thisHouse);
        }
        catch (io.grpc.StatusRuntimeException ex){
            Throwable cause = ex.getCause().getCause();

            //The house isn't online
            if(cause instanceof java.net.ConnectException){
                System.out.println("House " + super.getDestinationHouse().getId() + " is unexpectedly disconnected.");

                //Remove house locally
                Houses.getInstance().remove(super.getDestinationHouse());
            }
        }
        finally {
            super.getChannel().shutdown();
        }
    }
}
