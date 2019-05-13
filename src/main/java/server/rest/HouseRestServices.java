package server.rest;

import messages.StatisticMsgs;
import server.aspects.annotations.ProtoInput;
import server.aspects.annotations.Notification;
import messages.HouseMsgs.HouseInfoListMsg;
import messages.HouseMsgs.HouseInfoMsg;
import utility.Houses;

import javax.ws.rs.*;
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
    @Notification(text = "New house enter in the network.")
    public Response enter(InputStream inputStream) {
        HouseInfoMsg msg = null;
        try {
            msg = HouseInfoMsg.parseFrom(inputStream);
        } catch (IOException ex) {
            System.out.println(ex);
            return Response.status(500).build();
        }

        Set<HouseInfoMsg> houses;

        synchronized (Houses.getInstance()){
            if(!Houses.getInstance().add(msg))
                //423 is returned when the id is already present
                return Response.status(423).build();
            System.out.println("Casa aggiunta\n" + Houses.getInstance().size());
            houses = Houses.getInstance().getHouses();
        }

        //Remove current house
        houses.remove(msg);
        return Response.ok(HouseInfoListMsg.newBuilder().addAllHouse(houses).build().toByteArray()).build();

    }

    @POST
    @Path("sendstatistic")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @ProtoInput(proto = StatisticMsgs.StatisticHouseMsg.class)
    public Response sendStatistic(InputStream inputStream){

        return Response.ok().build();
    }

    @DELETE
    @Path("leave/{id}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Notification(text = "A house leaves the network.")
    public Response leave(@PathParam("id") int id){
        if(!Houses.getInstance().removeHouseFromId(id))
            //422 = Unprocessable Entity
            return Response.status(422).build();
        return Response.ok().build();
    }

    @DELETE
    @Path("unexpectedexit/{id}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Notification(text = "A house leaves the network.")
    public Response unexpectedExit(@PathParam("id") int id){
        if(!Houses.getInstance().removeHouseFromId(id))
            //422 = Unprocessable Entity
            return Response.status(422).build();
        return Response.ok().build();
    }
}
