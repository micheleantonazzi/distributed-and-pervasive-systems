package house.threads;

import house.HouseMain;
import messages.HouseMsgs.HouseInfoMsg;
import messages.StatisticMsgs;
import server.ServerMain;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RunnableBoostRequest implements Runnable {

    @Override
    public void run() {

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(ServerMain.SERVER_URI);

        HouseInfoMsg thisHouse = HouseMain.getHouseInfo();

        try {
            target.path("house/boostrequest").request().post(
                    Entity.entity(
                            HouseInfoMsg.newBuilder().setId(thisHouse.getId()).setAddress(thisHouse.getAddress())
                            .setPort(thisHouse.getPort()).build().toByteArray(),
                            MediaType.APPLICATION_OCTET_STREAM
                            ));;
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
        catch (Exception e){
            System.out.println(e);
        }

        finally {
            client.close();
        }
    }
}
