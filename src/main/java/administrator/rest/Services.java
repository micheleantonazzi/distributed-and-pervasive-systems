package administrator.rest;

import messages.administrator.NotificationOuterClass.*;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;

@Path("")
public class Services {

    @POST
    @Path("notification")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public void notification(InputStream stream){
        try{
            System.out.println(Notification.parseFrom(stream));
        }
        catch (IOException ex){
            System.out.println(ex);
        }
    }
}
