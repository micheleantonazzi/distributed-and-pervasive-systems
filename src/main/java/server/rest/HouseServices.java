package server.rest;

import server.Notification;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

@Path("house")
public class HouseServices {

    @POST
    @Path("enter")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Notification(text = "New house enter in the network")
    public void enter(InputStream inputStream){
        System.out.println("Una casa entra");
    }
}
