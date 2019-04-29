package server.rest;

import aspects.annotations.ProtoInput;
import aspects.annotations.Notification;
import messages.HouseMsgs.HouseInfoMsg;
import server.ServerMain;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Path("house")
public class HouseRestServices {

    @POST
    @Path("enter")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @ProtoInput(proto = HouseInfoMsg.class)
    @Notification(text = "New house enter in the network.", checkInput = true)
    public Response enter(InputStream inputStream){
        HouseInfoMsg msg = null;
        try {
            msg = HouseInfoMsg.parseFrom(inputStream);
        } catch (IOException ex) {
            System.out.println(ex);
            return Response.status(500).build();
        }

        Set<HouseInfoMsg> houses = ServerMain.getInstance().getHouses();
        for (HouseInfoMsg house : houses){
            if (house.getId() == msg.getId())
                return Response.status(423).build();
        }
        ServerMain.getInstance().addHouse(msg);
        return Response.ok().build();
    }
}
