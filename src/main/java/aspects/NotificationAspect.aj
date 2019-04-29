package aspects;

import aspects.annotations.Notification;
import messages.AdministratorInfoMsgOuterClass.AdministratorInfoMsg;
import server.ServerMain;
import server.threads.RunnableNotification;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Set;


//This aspect sends automatically notifications
public aspect NotificationAspect {

    pointcut sendNotification(InputStream inputStream, Notification notification):
            execution(* *..*.*(..)) && !within(aspects.NotificationAspect) && args(inputStream)
            && @annotation(notification);

    after(InputStream inputStream, Notification notification) returning(Object ret): sendNotification(inputStream, notification){
        Response response = (Response) ret;
        if(response.getStatus() == 200){
            Set<AdministratorInfoMsg> administrators = ServerMain.getInstance().getAdministrators();
            for(AdministratorInfoMsg connectionInfoMsg : administrators)
                new Thread(new RunnableNotification(connectionInfoMsg, notification.text())).start();
        }
    }
}
