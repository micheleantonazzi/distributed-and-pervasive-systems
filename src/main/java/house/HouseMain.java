package house;

import house.smartmeter.BufferSynchronized;
import house.smartmeter.SmartMeterSimulator;
import house.threads.ThreadReadMeasurements;
import house.threads.methods.ThreadSayGoodbye;
import house.threads.methods.ThreadSayHello;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import messages.HouseMsgs.HouseInfoListMsg;
import messages.HouseMsgs.HouseInfoMsg;
import server.ServerMain;
import utility.HousesAndStatistics;

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

public class HouseMain {

    //STATIC VARIABLES
    private static HouseMain instance;
    private static String ADDRESS = "localhost";
    private static int PORT = 8888;
    private static int ID;
    private static HouseInfoMsg HOUSE_INFO;
    private static ThreadReadMeasurements THREAD_READ_MEASUREMENTS = new ThreadReadMeasurements();
    private static SmartMeterSimulator THREAD_SMART_METER = new SmartMeterSimulator(BufferSynchronized.getInstance());

    private HouseMain(){}

    public synchronized static HouseMain getInstance(){
        if(instance == null)
            instance = new HouseMain();
        return instance;
    }

    public static void main(String[] args) {

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(ServerMain.SERVER_URI);
        Server server = null;

        try{
            boolean retry = false;


            do{
                try {
                    server = ServerBuilder.forPort(PORT).addService(new HouseGrpcServices()).build();
                    server.start();
                    retry = true;
                }
                catch (IOException ex){
                    //In this case the server failed because the port is already used
                    System.out.println("Port " + PORT + " is already used.\nRetry.");
                    PORT += 1;
                }

            }
            while (!retry);

            ID = (ADDRESS + PORT + Math.random()).hashCode();

            System.out.println("House with id " + ID + " is running at port " + PORT);

            HOUSE_INFO = HouseInfoMsg.newBuilder().setId(ID).setAddress(ADDRESS).setPort(PORT).build();

            Response response = target.path("house/enter").request().post(
                    Entity.entity(HOUSE_INFO.toByteArray(),
                            MediaType.APPLICATION_OCTET_STREAM));

            List<HouseInfoMsg> houses = HouseInfoListMsg.parseFrom(response.readEntity(InputStream.class)).getHouseList();

            HousesAndStatistics.getInstance().setHouses(houses);
            HousesAndStatistics.getInstance().addHouse(HOUSE_INFO);

            //Say hello to other houses
            for (HouseInfoMsg house : HousesAndStatistics.getInstance().getOtherHouses())
                new ThreadSayHello(HOUSE_INFO, house).start();


            //Start smartMeter
            THREAD_SMART_METER.start();
            THREAD_READ_MEASUREMENTS.start();

            //buffered reader to read from standard input
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String input = " ";
            while(!input.equals("x")) {
                System.out.println("Type:\n" +
                        "\t- 0 to get houses list\n" +
                        "\t- x to close the application");
                input = reader.readLine();
                if (input.equals("0")) {
                    System.out.println(HousesAndStatistics.getInstance().getHouses());
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
        finally {
            //Disconnect to the network
            target.path("house/leave/" + ID).request().delete();

            THREAD_SMART_METER.stopMeGently();
            THREAD_READ_MEASUREMENTS.stop();

            for (HouseInfoMsg house : HousesAndStatistics.getInstance().getOtherHouses())
                new ThreadSayGoodbye(HOUSE_INFO, house).start();

            client.close();

            if(server != null) {
                server.shutdown();
            }

        }
    }

    public static HouseInfoMsg getHouseInfo(){
        synchronized (HOUSE_INFO){
            return HOUSE_INFO;
        }
    }
}
