package server.rest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import messages.house.HouseOuterClass.*;

import messages.server.ConnectionInfoMsgOuterClass.*;
import server.ServerMain;


import java.io.IOException;
import java.io.InputStream;

@Path("administrator")
public class AdministratorServices {

    //To register the administrator client and send notification to them
    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Path("connect")
    public void connect(InputStream stream){
        try{
            ServerMain.getInstance().addAdministrator(ConnectionInfoMsg.parseFrom(stream));
        }catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }


    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("hello")
    public Response hello(){
        House house = House.newBuilder().setId("2").build();
        return Response.ok(house.toByteArray()).build();
    }
}

