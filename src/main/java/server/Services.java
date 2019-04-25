package server;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import messages.house.HouseOuterClass.*;

@Path("/helloworld")
public class Services {

    @GET
    @Produces("text/plain")
    public String helloWorld(){
        return "Hello world!";
    }

    @GET
    @Produces("application/octet-stream")
    @Path("hello")
    public Response hello(){
        House house = House.newBuilder().setId("2").build();
        return Response.ok(house.toByteArray()).build();
    }

    @GET
    @Produces("text/plain")
    @Path("hello2")
    public String hello2(){
        House house = House.newBuilder().setId("2").build();
        return "ciao";
    }
}

