package house;

import house.services.HouseServicesGrpc;
import house.services.HouseServicesGrpc.HouseServicesBlockingStub;
import house.threads.RunnableSayHello;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import messages.HouseMsgs.HouseInfoListMsg;
import messages.HouseMsgs.HouseInfoMsg;
import server.ServerMain;
import utility.Houses;

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
    private static HouseMain instance;
    private static String ADDRESS = "localhost";
    private static int PORT = 8888;
    private static int ID;
    private static HouseInfoMsg HOUSE_INFO;

    private HouseMain(){}

    public static HouseMain getInstance(){
        if(instance == null)
            instance = new HouseMain();
        return instance;
    }

    public static void main(String[] args) {

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(ServerMain.SERVER_URI);


        try{
            boolean retry = false;
            Server server;

            do{
                try {
                    server = ServerBuilder.forPort(PORT).addService(new HouseGrpcServices()).build().start();
                    retry = true;
                }
                catch (IOException ex){
                    //In this case the server failed because the port is already used
                    System.out.println("Port " + PORT + " is already used.\nRetry.");
                    PORT += 1;
                }
            }
            while (!retry);

            System.out.println("House is running at port " + PORT);

            ID = (ADDRESS + PORT).hashCode();

            HOUSE_INFO = HouseInfoMsg.newBuilder().setId(ID).setAddress(ADDRESS).setPort(PORT).build();

            Response response = target.path("house/enter").request().post(
                    Entity.entity(HOUSE_INFO.toByteArray(),
                            MediaType.APPLICATION_OCTET_STREAM));

            Houses.getInstance().setHouses(HouseInfoListMsg.parseFrom(response.readEntity(InputStream.class)).getHouseList());

            //Say hello to other houses
            for (HouseInfoMsg house : Houses.getInstance().getSet()){
                new Thread(new RunnableSayHello(HOUSE_INFO, house)).start();

            }


            //buffered reader to read from standard input
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String input = " ";
            while(!input.equals("x")) {
                System.out.println("Type:\n" +
                        "\t- 0 to get houses list\n" +
                        "\t- x to close the application");
                input = reader.readLine();
                if (input.equals("0")) {
                    System.out.println(Houses.getInstance().getSet());
                }
            }
        }
        catch (ProcessingException ex){
            Throwable cause = ex.getCause();
            System.out.println(ex);
            System.out.println(cause);
            if (cause.getClass().getName().equals("java.net.ConnectException"))
                //if the connection doesn't exist maybe the server isn't running
                System.out.println("ERROR, the server couldn't be contacted, please check if it running.");
        }
        catch (IOException ex){
            System.out.println(ex);
        }
        catch (Exception ex){
            System.out.println(ex);
        }
        finally {
            //Disconnect to the network
            target.path("house/leave/" + ID).request().delete();
        }
    }
}
