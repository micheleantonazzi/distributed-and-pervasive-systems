package administrator.rest;

import server.aspects.annotations.ProtoInput;
import messages.NotificationMsgOuterClass.NotificationMsg;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

@Path("")
public class RestServices {

    @POST
    @Path("notification")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @ProtoInput(proto = NotificationMsg.class)
    public Response notification(InputStream stream){
        try{
            System.out.println(NotificationMsg.parseFrom(stream).getText());
        }
        catch (IOException ex){
            System.out.println(ex);
            return Response.status(500).build();
        }
        return Response.ok().build();
    }
}
