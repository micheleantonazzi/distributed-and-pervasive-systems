package house;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import messages.HouseMsgs.HouseInfoMsg;
import server.ServerMain;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HouseMain {

    //STATIC VARIABLES
    private static String ADDRESS = "localhost";
    private static int PORT = 8888;
    public static void main(String[] args) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(ServerMain.SERVER_URI);
        try{
            Server server = ServerBuilder.forPort(PORT).addService(new HouseRpcServices()).build();
            Response response = target.path("house/enter").request().post(
                    Entity.entity(HouseInfoMsg.newBuilder().setId((ADDRESS + PORT).hashCode()).setAddress(ADDRESS).setPort(PORT).build().toByteArray(),
                            MediaType.APPLICATION_OCTET_STREAM));

            System.out.println(response.getStatus());

            //buffered reader to read from standard input
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String input = " ";
            while(!input.equals("x")) {
                System.out.println("Type:\n" +
                        "\t- 0 to get houses list\n" +
                        "\t- x to close the application");
                input = reader.readLine();
                if (input.equals("0")) {

                }
            }
        }
        catch (ProcessingException ex){
            Throwable cause = ex.getCause();
            if (cause.getClass().getName().equals("java.net.ConnectException"))
                //if the connection doesn't exist maybe the server isn't running
                System.out.println("ERROR, the server couldn't be contacted, please check if it running.");
        }
        catch (IOException ex){
            System.out.println(ex);
        }
    }
}
