package administrator;

import messages.server.ConnectionInfoOuterClass.*;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;


public class AdministratorMain {
    public static final String serverAddress = "localhost";
    public static final  int serverPort = 11111;
    public static void main(String[] args) throws IOException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://"+ serverAddress + ":" + serverPort + "/sdp-server/");

        /*Response house = target.path("helloworld/hello").request().get();
        System.out.println(House.parseFrom(house.readEntity(InputStream.class)));*/

        try{
            //Registration to server
            target.path("administrator/connect").request().post(
                    Entity.entity(ConnectionInfo.newBuilder()
                            .setAddress(serverAddress).setPort(serverPort).build().toByteArray(), MediaType.APPLICATION_OCTET_STREAM));
        }catch (ProcessingException ex){
            System.out.println("ERROR, the server couldn't be contacted.\n" + ex.getMessage());
        }

    }
}
