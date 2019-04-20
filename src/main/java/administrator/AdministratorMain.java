package administrator;

import messages.house.HouseOuterClass.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

public class AdministratorMain {
    public static void main(String[] args) throws IOException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:11111/sdp-server/");

        Response house = target.path("helloworld/hello").request().get();
        System.out.println(House.parseFrom(house.readEntity(InputStream.class)));
    }
}
