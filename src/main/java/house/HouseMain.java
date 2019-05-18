package house;

import house.smartmeter.BufferSynchronized;
import house.smartmeter.SmartMeterSimulator;
import house.threads.ThreadGlobalStatistic;
import house.threads.ThreadReadMeasurements;
import house.threads.grpc.methods.ThreadSayGoodbye;
import house.threads.grpc.methods.ThreadSayHello;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import messages.HouseMsgs.HouseInfoListMsg;
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
    private static ThreadGlobalStatistic THREAD_GLOBAL_STATISTIC = new ThreadGlobalStatistic();

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

            Response response = Response.ok().build();
            ID = 1;

            do{
                try {
                    server = ServerBuilder.forPort(PORT).addService(new HouseGrpcServices()).build();
                    server.start();
                    retry = true;

                    HOUSE_INFO = HouseInfoMsg.newBuilder().setId(ID).setAddress(ADDRESS).setPort(PORT).build();

                    response = target.path("house/enter").request().post(
                            Entity.entity(HOUSE_INFO.toByteArray(),
                                    MediaType.APPLICATION_OCTET_STREAM));
                    if(response.getStatus() != 200){
                        server.shutdown();
                        retry = false;
                        ID++;
                    }
                }
                catch (IOException ex){
                    //In this case the server failed because the port is already used
                    System.out.println("Port " + PORT + " is already used.\nRetry.");
                    PORT++;
                }

            }
            while (!retry);

            System.out.println("House with id " + ID + " is running at port " + PORT);

            List<HouseInfoMsg> houses = HouseInfoListMsg.parseFrom(response.readEntity(InputStream.class)).getHouseList();

            HousesAndStatistics.getInstance().setHouses(houses);
            Coordinator.getInstance().setHouses(houses);
            HousesAndStatistics.getInstance().addHouse(HOUSE_INFO);

            //Say hello to other houses
            for (HouseInfoMsg house : HousesAndStatistics.getInstance().getOtherHouses())
                new ThreadSayHello(HOUSE_INFO, house).start();


            //Start smartMeter
            THREAD_SMART_METER.start();
            THREAD_READ_MEASUREMENTS.start();
            THREAD_GLOBAL_STATISTIC.start();


            //buffered reader to read from standard input
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String input = " ";
            while(!input.equals("x")) {
                System.out.println("Type:\n" +
                        "\t- 0 to get houses list\n" +
                        "\t- 1 to ask boost\n" +
                        "\t- x to close the application");
                input = reader.readLine();
                if (input.equals("0")) {
                    System.out.println(HousesAndStatistics.getInstance().getOtherHouses());
                }
                else if(input.equals("1")){
                    BoostCoordinator.getInstance().tryAcquireBoost();
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
            THREAD_READ_MEASUREMENTS.stopAndClose();
            THREAD_GLOBAL_STATISTIC.stopAndClose();
            BoostCoordinator.getInstance().stopAndClose();

            // Stop all threads that send statistic
            HousesAndStatistics.getInstance().stopAll();

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

    public static SmartMeterSimulator getSmartMeter(){
        return THREAD_SMART_METER;
    }
}
