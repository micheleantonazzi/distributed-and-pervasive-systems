package administrator;

import messages.AdministratorInfoMsgOuterClass.AdministratorInfoMsg;
import messages.HouseMsgs.HouseInfoMsg;
import messages.HouseMsgs.HouseInfoListMsg;
import messages.StatisticMsgs.StatisticsAverageAndDeviationMsg;
import messages.StatisticMsgs.StatisticMsg;
import messages.StatisticMsgs.StatisticListMsg;
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
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;


public class AdministratorMain {

    //Address of server to receive notification
    private static final String CLIENT_ADDRESS = "localhost";
    private static int CLIENT_PORT = 11122;
    public static String CLIENT_URI = "http://" + CLIENT_ADDRESS + ":" + CLIENT_PORT + "/administrator/";

    public static void main(String[] args) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(ServerMain.SERVER_URI);
        HttpServer server = null;

        //the cycle serves to find a free door if the initial one in occupied
        boolean retry;
        do{
            retry = false;
            try{
                //try to start the server to receive notification
                final ResourceConfig resourceConfig = new ResourceConfig().packages("administrator.rest");
                server = GrizzlyHttpServerFactory.createHttpServer(URI.create(CLIENT_URI), resourceConfig);

                //Registration to the server
                Response response = target.path("administrator/connect").request().post(
                        Entity.entity(AdministratorInfoMsg.newBuilder()
                                        .setAddress(CLIENT_ADDRESS).setPort(CLIENT_PORT).build().toByteArray(),
                                MediaType.APPLICATION_OCTET_STREAM));

                if(response.getStatus() != 200){
                    throw new Exception("Connection to server failed, response status: " + response.getStatus());
                }

                System.out.println(String.format("Client running at " + CLIENT_URI + "\n"));

                //buffered reader to read from standard input
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                String input = " ";
                while(!input.equals("x")){
                    System.out.println("Type:\n" +
                            "\t- 0 to get houses list\n" +
                            "\t- 1 to get the latest N statistics of a home\n" +
                            "\t- 2 to get average and standard deviation of latest N statistics of a home\n" +
                            "\t- x to close the application");
                    input = reader.readLine();
                    if (input.equals("0")){
                        response = target.path("administrator/houses").request().get();
                        if(response.getStatus() != 200)
                            System.out.println("Request failed, response status: " + response.getStatus());
                        else{
                            HouseInfoListMsg houses = HouseInfoListMsg.parseFrom(response.readEntity(InputStream.class));
                            for(HouseInfoMsg house : houses.getHouseList())
                                System.out.println("- House: id = " + house.getId());
                        }
                    }
                    else if(input.equals("1")){
                        System.out.println("Insert house id");
                        String houseId = reader.readLine();
                        System.out.println("Insert number of statistics");
                        String number = reader.readLine();
                        response = target.path("administrator/statistics/" + houseId + "/" + number).request().get();
                        if(response.getStatus() != 200)
                            System.out.println("Request failed, response status: " + response.getStatus());
                        else{
                            StatisticListMsg statistics = StatisticListMsg.parseFrom(response.readEntity(InputStream.class));
                            for(StatisticMsg statistic : statistics.getStatisticList())
                                System.out.println("- " + statistic.getTimestamp() +  " -> " + statistic.getValue());
                        }
                    }
                    else if(input.equals("2")){
                        System.out.println("Insert house id");
                        String houseId = reader.readLine();
                        System.out.println("Insert number of statistics");
                        String number = reader.readLine();
                        response = target.path("administrator/averagedeviation/" + houseId + "/" + number).request().get();
                        if(response.getStatus() != 200)
                            System.out.println("Request failed, response status: " + response.getStatus());
                        else{
                            StatisticsAverageAndDeviationMsg averageAndDeviation = StatisticsAverageAndDeviationMsg
                                    .parseFrom(response.readEntity(InputStream.class));
                            System.out.println("Average:  " + averageAndDeviation.getAverage() +  "\n" +
                                    "Standard deviation: " + averageAndDeviation.getDeviation());
                        }
                    }
                }

                //Disconnect to the server
                target.path("administrator/disconnect").request().post(
                        Entity.entity(AdministratorInfoMsg.newBuilder()
                                        .setAddress(CLIENT_ADDRESS).setPort(CLIENT_PORT).build().toByteArray(),
                                MediaType.APPLICATION_OCTET_STREAM));

            }catch (ProcessingException ex){
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
                System.out.println("User's input error");
            }
            catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }while (retry);


        //CLOSE
        client.close();
        if(server != null)
            server.shutdownNow();
    }
}
