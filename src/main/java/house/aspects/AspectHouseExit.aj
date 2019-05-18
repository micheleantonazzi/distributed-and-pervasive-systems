package house.aspects;

import house.threads.grpc.ThreadGrpc;
import house.HousesAndStatistics;
import server.ServerMain;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public aspect AspectHouseExit {

    pointcut HouseExit(ThreadGrpc threadGrpc):
            execution(* house.threads.grpc.ThreadGrpc.run()) && this(threadGrpc);

    Object around(ThreadGrpc threadGrpc): HouseExit(threadGrpc){
        try{
            return proceed(threadGrpc);
        }
        catch (Exception ex){
            if (ex instanceof io.grpc.StatusRuntimeException){
                Throwable cause = ex.getCause().getCause();

                //The house isn't online
                if(cause instanceof java.net.ConnectException){
                    System.out.println("House " + threadGrpc.getDestinationHouse().getId() + " is unexpectedly disconnected.");

                    //Remove house locally
                    HousesAndStatistics.getInstance().removeHouse(threadGrpc.getDestinationHouse());


                    Client client = ClientBuilder.newClient();
                    WebTarget target = client.target(ServerMain.SERVER_URI);
                    try{
                        target.path("house/unexpectedexit/" + threadGrpc.getDestinationHouse().getId())
                                .request().delete();
                    }
                    catch (ProcessingException e){
                        if(e.getClass().getName().equals("java.net.ConnectException"))
                            System.out.println("ERROR, the server couldn't be contacted, please check if it running.");
                        else
                            System.out.println(e);
                    }
                    finally {
                        client.close();
                    }
                }
            }
            else
                System.out.println(ex);
        }
        finally {
            threadGrpc.getChannel().shutdown();
        }
        return new Object();
    }
}
