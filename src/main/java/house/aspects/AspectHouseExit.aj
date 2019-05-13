package house.aspects;

import house.threads.grpc.ThreadGrpc;
import utility.HousesAndStatistics;

public aspect AspectHouseExit {

    pointcut HouseExit(ThreadGrpc threadGrpc):
            execution(* house.threads.grpc.ThreadGrpc.run()) && this(threadGrpc);

    Object around(ThreadGrpc threadGrpc): HouseExit(threadGrpc){
        System.out.println("aspecy");
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
