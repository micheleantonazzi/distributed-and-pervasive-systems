package house.threads;

import messages.StatisticMsgs.StatisticMsg;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class RunnableSendGlobalStatistic implements Runnable {

    private StatisticMsg statistic;

    private WebTarget target;

    public RunnableSendGlobalStatistic(StatisticMsg statistic, WebTarget target){
        this.statistic = statistic;
        this.target = target;
    }

    @Override
    public void run() {
        try {
            target.path("house/sendglobalstatistic").request().post(
                    Entity.entity(
                            this.statistic.toByteArray(),
                            MediaType.APPLICATION_OCTET_STREAM));
        } catch (ProcessingException ex) {
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
