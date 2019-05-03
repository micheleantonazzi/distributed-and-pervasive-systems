package house;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import messages.HouseMsgs.HouseInfoListMsg;
import messages.HouseMsgs.HouseInfoMsg;
import server.ServerMain;
import utility.HashSetSynchronized;

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
import java.util.List;
import java.util.Set;

public class HouseMain {

    //STATIC VARIABLES
    private static HouseMain instance;
    private static String ADDRESS = "localhost";
    private static int PORT = 8888;
    private static int ID;

    //INSTANCE VARIABLES
    private HashSetSynchronized<HouseInfoMsg> houses = new HashSetSynchronized<>();

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
            Server server = ServerBuilder.forPort(PORT).addService(new HouseRpcServices()).build();

            ID = (ADDRESS + PORT).hashCode();
            Response response = target.path("house/enter").request().post(
                    Entity.entity(HouseInfoMsg.newBuilder().setId(ID).setAddress(ADDRESS).setPort(PORT).build().toByteArray(),
                            MediaType.APPLICATION_OCTET_STREAM));

            HouseMain.getInstance().setHouses(HouseInfoListMsg.parseFrom(response.readEntity(InputStream.class)).getHouseList());

            //buffered reader to read from standard input
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String input = " ";
            while(!input.equals("x")) {
                System.out.println("Type:\n" +
                        "\t- 0 to get houses list\n" +
                        "\t- x to close the application");
                input = reader.readLine();
                if (input.equals("0")) {
                    System.out.println(HouseMain.getInstance().getHouses());
                }
            }

            //Disconnect to the network
            target.path("house/leave/" + ID).request().delete();
        }
        catch (ProcessingException ex){
            Throwable cause = ex.getCause();
            if (cause.getClass().getName().equals("java.net.ConnectException"))
                //if the connection doesn't exist maybe the server isn't running
                System.out.println("ERROR, the server couldn't be contacted, please check if it running.");
            System.out.println("ssss");
        }
        catch (IOException ex){
            System.out.println(ex);
        }
    }

    public void setHouses(List<HouseInfoMsg> list){
        this.houses = new HashSetSynchronized<>(list);
    }

    public Set<HouseInfoMsg> getHouses(){
        return this.houses.getSet();
    }
}
