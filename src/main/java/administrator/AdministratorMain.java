package administrator;

import messages.server.ConnectionInfoOuterClass.*;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import server.ServerMain;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;


public class AdministratorMain {

    //Address of server to receive notification
    private static final String CLIENT_ADDRESS = "localhost";
    private static int CLIENT_PORT = 11121;
    public static String CLIENT_URI = "http://" + CLIENT_ADDRESS + ":" + CLIENT_PORT + "/administrator/";

    public static void main(String[] args) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(ServerMain.SERVER_URI);
        HttpServer server = null;

        /*Response house = target.path("helloworld/hello").request().get();
        System.out.println(House.parseFrom(house.readEntity(InputStream.class)));*/

        //the cycle serves to find a free door if the initial one in occupied
        boolean retry;
        do{
            retry = false;
            try{
                //try to start the server to receive notification
                final ResourceConfig resourceConfig = new ResourceConfig().packages("administrator.rest");
                server = GrizzlyHttpServerFactory.createHttpServer(URI.create(CLIENT_URI), resourceConfig);

                //Registration to the server
                target.path("administrator/connect").request().post(
                        Entity.entity(ConnectionInfo.newBuilder()
                                        .setAddress(CLIENT_ADDRESS).setPort(CLIENT_PORT).build().toByteArray(),
                                MediaType.APPLICATION_OCTET_STREAM));
                System.out.println(String.format("Client running at " + CLIENT_URI + "\n"));


                System.in.read();

            }catch (ProcessingException ex){
                System.out.println(ex.getCause().getClass());
                Throwable cause = ex.getCause();
                switch (cause.getClass().getName()){
                    //if the connection doesn't exist maybe the server isn't running
                    case "java.net.ConnectException":
                        System.out.println("ERROR, the server couldn't be contacted, please check if it running.");
                        break;

                    //when starting multiple clients the port must change
                    case "java.net.BindException":
                        System.out.println(CLIENT_URI + " already used, retry changing port.");
                        CLIENT_PORT++;
                        CLIENT_URI = "http://" + CLIENT_ADDRESS + ":" + CLIENT_PORT + "/administrator/";
                        retry = true;
                        break;
                }
            }
            catch (IOException ex){
                System.out.println("Input error");
            }
        }while (retry);


        //CLOSE
        client.close();
        if(server != null)
            server.shutdownNow();
    }
}
