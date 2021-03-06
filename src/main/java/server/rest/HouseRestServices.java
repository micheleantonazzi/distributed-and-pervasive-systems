package server.rest;

import messages.StatisticMsgs.StatisticMsg;
import messages.StatisticMsgs.StatisticHouseMsg;
import server.GlobalStatistics;
import server.aspects.CheckInputREST;
import server.aspects.annotations.ProtoInput;
import server.aspects.annotations.Notification;
import messages.HouseMsgs.HouseInfoListMsg;
import messages.HouseMsgs.HouseInfoMsg;
import server.Houses;

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
            return Response.status(400).build();
        }

        Set<HouseInfoMsg> houses;

        synchronized (Houses.getInstance()){
            if(!Houses.getInstance().add(msg))
                //423 is returned when the id is already present
                return Response.status(423).build();
            houses = Houses.getInstance().getHouses();
        }

        //Remove current house
        houses.remove(msg);
        return Response.ok(HouseInfoListMsg.newBuilder().addAllHouse(houses).build().toByteArray()).build();
    }

    @POST
    @Path("sendstatistic")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @ProtoInput(proto = StatisticHouseMsg.class)
    public Response sendStatistic(InputStream inputStream){

        StatisticHouseMsg statisticHouseMsg;
        try {
            statisticHouseMsg = StatisticHouseMsg.parseFrom(inputStream);
        } catch (IOException ex) {
            System.out.println(ex);
            return Response.status(400).build();
        }
        HouseInfoMsg house = statisticHouseMsg.getHouseInfo();
        StatisticMsg statistic = statisticHouseMsg.getStatistic();

        if(!Houses.getInstance().addStatistic(house, statistic))
            return Response.status(400).build();

        return Response.ok().build();
    }

    @POST
    @Path("sendglobalstatistic")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @ProtoInput(proto = StatisticMsg.class)
    public Response sendGlobalStatistic(InputStream inputStream){
        StatisticMsg statistic;

        try {
            statistic = StatisticMsg.parseFrom(inputStream);
        } catch (IOException ex) {
            System.out.println(ex);
            return Response.status(400).build();
        }

        GlobalStatistics.getInstance().add(statistic);

        return Response.ok().build();
    }


    @DELETE
    @Path("leave/{id}")
    @Notification(text = "A house leaves the network.")
    public Response leave(@PathParam("id") int id){
        if(!Houses.getInstance().remove(id))
            //422 = Unprocessable Entity
            return Response.status(422).build();
        return Response.ok().build();
    }

    @DELETE
    @Path("unexpectedexit/{id}")
    @Notification(text = "A house leaves unexpectedly the network.")
    public Response unexpectedlyExit(@PathParam("id") int id){
        if(!Houses.getInstance().remove(id))
            //422 = Unprocessable Entity
            return Response.status(422).build();
        return Response.ok().build();
    }

    @POST
    @Path("boostrequest")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @ProtoInput(proto = HouseInfoMsg.class)
    @Notification(text = "A house requests the boost")
    public Response boostRequest(InputStream inputStream){
        return Response.ok().build();
    }
}
