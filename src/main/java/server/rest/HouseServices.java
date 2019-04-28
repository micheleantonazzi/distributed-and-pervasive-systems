package server.rest;

import aspects.annotations.ProtoInput;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import messages.server.ConnectionInfoMsgOuterClass;
import messages.server.ConnectionInfoMsgOuterClass.*;
import aspects.annotations.Notification;
import server.ServerMain;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

@Path("house")
public class HouseServices {

    @POST
    @Path("enter")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @ProtoInput(proto = ConnectionInfoMsg.class)
    @Notification(text = "New house enter in the network.")
    public Response enter(InputStream inputStream){
        ConnectionInfoMsg msg = null;
        try {
            msg = ConnectionInfoMsg.parseFrom(inputStream);
        } catch (IOException ex) {
            System.out.println(ex);
            return Response.status(500).build();
        }
        ServerMain.getInstance().addHouse(msg);
        return Response.ok().build();
    }
}
