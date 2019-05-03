package server.rest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import aspects.annotations.ProtoInput;
import messages.AdministratorInfoMsgOuterClass.AdministratorInfoMsg;
import messages.HouseMsgs.HouseInfoListMsg;
import server.Houses;
import server.ServerMain;


import java.io.IOException;
import java.io.InputStream;

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
            ServerMain.getInstance().addAdministrator(administrator);
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
            System.out.println(ServerMain.getInstance().removeAdministrator(administratorInfoMsg));
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
        HouseInfoListMsg houseList = HouseInfoListMsg.newBuilder().addAllHouse(Houses.getInstance().getSet()).build();
        return Response.ok(houseList.toByteArray(), MediaType.APPLICATION_OCTET_STREAM).build();
    }
}

