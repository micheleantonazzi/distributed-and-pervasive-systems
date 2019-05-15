package house.threads;

import house.HouseMain;
import messages.StatisticMsgs;
import messages.StatisticMsgs.StatisticHouseMsg;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class RunnableSendStatistic implements Runnable {

    private StatisticHouseMsg statisticHouseMsg;

    private WebTarget target;

    public RunnableSendStatistic(StatisticHouseMsg statisticHouseMsg, WebTarget target){
        this.statisticHouseMsg = statisticHouseMsg;
        this.target = target;
    }

    @Override
    public void run(){
        try{
            target.path("house/sendstatistic").request().post(
                    Entity.entity(
                            this.statisticHouseMsg.toByteArray(),
                            MediaType.APPLICATION_OCTET_STREAM));
        }
        catch (ProcessingException ex){
            Throwable cause = ex.getCause();
            System.out.println(ex);
            System.out.println(cause);
            if (cause.getClass().getName().equals("java.net.ConnectException"))
                //if the connection doesn't exist maybe the server isn't running
                System.out.println("ERROR, the server couldn't be contacted, please check if it running.");
            else
                System.out.println(ex);
        }
    }
}
