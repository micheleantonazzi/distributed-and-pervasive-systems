package server.rest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import aspects.annotations.ProtoInput;
import messages.AdministratorInfoMsgOuterClass.AdministratorInfoMsg;
import messages.HouseInfoMsgOuterClass.HouseInfoMsg;
import server.ServerMain;


import java.io.IOException;
import java.io.InputStream;

@Path("administrator")
public class AdministratorServices {

    //To register the administrator client and send notification to them
    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Path("connect")
    @ProtoInput(proto = AdministratorInfoMsg.class)
    public Response connect(InputStream stream){
        try{
            ServerMain.getInstance().addAdministrator(AdministratorInfoMsg.parseFrom(stream));
        }catch (IOException ex){
            System.out.println(ex.getMessage());
            return Response.status(500).build();
        }
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Path("houses")
    @ProtoInput(proto = HouseInfoMsg.class)
    public Response houses(InputStream inputStream){
        return Response.ok().build();
    }
}

