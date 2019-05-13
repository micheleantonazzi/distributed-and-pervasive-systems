package server.rest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import messages.StatisticMsgs.StatisticsAverageAndDeviationMsg;
import messages.StatisticMsgs.StatisticMsg;
import messages.StatisticMsgs.StatisticListMsg;
import org.javatuples.Pair;
import server.aspects.annotations.ProtoInput;
import messages.AdministratorInfoMsgOuterClass.AdministratorInfoMsg;
import messages.HouseMsgs.HouseInfoListMsg;
import server.Administrators;
import utility.Houses;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Path("administrator")
public class AdministratorRestServices {

    //To register the administrator client and send notification to them
    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Path("connect")
    @ProtoInput(proto = AdministratorInfoMsg.class)
    public Response connect(InputStream stream){
        try{
            AdministratorInfoMsg administrator = AdministratorInfoMsg.parseFrom(stream);
            Administrators.getInstance().add(administrator);
        }catch (IOException ex){
            System.out.println(ex.getMessage());
            return Response.status(500).build();
        }
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Path("disconnect")
    @ProtoInput(proto = AdministratorInfoMsg.class)
    public Response disconnect(InputStream inputStream){
        try {
            AdministratorInfoMsg administratorInfoMsg = AdministratorInfoMsg.parseFrom(inputStream);
            System.out.println(Administrators.getInstance().remove(administratorInfoMsg));
        } catch (IOException ex) {
            System.out.println(ex);
            return Response.status(500).build();
        }
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("houses")
    public Response houses(){
        HouseInfoListMsg houseList = HouseInfoListMsg.newBuilder().addAllHouse(Houses.getInstance().getHouses()).build();
        return Response.ok(houseList.toByteArray(), MediaType.APPLICATION_OCTET_STREAM).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("statistics/{house}/{number}")
    public Response getStatistics(@PathParam("house") int house, @PathParam("number") int number){
        List<StatisticMsg> statistics = Houses.getInstance().getStatistics(house, number);

        if(statistics == null)
            return Response.status(400).build();

        return Response.ok(
                StatisticListMsg.newBuilder().addAllStatistic(statistics).build().toByteArray(),
                MediaType.APPLICATION_OCTET_STREAM).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("averagedeviation/{house}/{number}")
    public Response getAverageAndDeviation(@PathParam("house") int house, @PathParam("number") int number){
        Pair<Double, Double> ret = Houses.getInstance().getAverangeAndDeviation(house, number);

        if(ret == null)
            return Response.status(400).build();

        return Response.ok(
                StatisticsAverageAndDeviationMsg.newBuilder()
                        .setAverage(ret.getValue0()).setDeviation(ret.getValue1()).build().toByteArray(),
                MediaType.APPLICATION_OCTET_STREAM).build();
    }
}

