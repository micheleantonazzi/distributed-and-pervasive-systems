package house;

import house.threads.RunnableGrpc;
import utility.Houses;

public aspect AspectHouseExit {

    pointcut HouseExit(RunnableGrpc runnableGrpc):
            execution(* house.threads.RunnableGrpc.run()) && this(runnableGrpc);

    Object around(RunnableGrpc runnableGrpc): HouseExit(runnableGrpc){
        try{
            return proceed(runnableGrpc);
        }
        catch (Exception ex){
            if (ex instanceof io.grpc.StatusRuntimeException){
                Throwable cause = ex.getCause().getCause();

                //The house isn't online
                if(cause instanceof java.net.ConnectException){
                    System.out.println("House " + runnableGrpc.getDestinationHouse().getId() + " is unexpectedly disconnected.");

                    //Remove house locally
                    Houses.getInstance().remove(runnableGrpc.getDestinationHouse());
                }
            }
            else
                System.out.println(ex);
        }
        finally {
            runnableGrpc.getChannel().shutdown();
        }
        return new Object();
    }
}
