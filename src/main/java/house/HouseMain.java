package house;

import messages.AdministratorInfoMsgOuterClass.AdministratorInfoMsg;
import server.ServerMain;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class HouseMain {
    public static void main(String[] args) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(ServerMain.SERVER_URI);
        try{
            target.path("house/enter").request().post(
                    Entity.entity(AdministratorInfoMsg.newBuilder().setAddress("localhost").setPort(11134).build().toByteArray(),
                            MediaType.APPLICATION_OCTET_STREAM));
        }
        catch (ProcessingException ex){
            System.out.println(ex);
        }
    }
}
