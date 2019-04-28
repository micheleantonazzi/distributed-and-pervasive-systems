package aspects;

import aspects.annotations.Notification;
import messages.server.ConnectionInfoMsgOuterClass.*;
import server.ServerMain;
import server.threads.RunnableNotification;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;


//This aspect sends automatically notifications
public aspect NotificationAspect {

    pointcut sendNotification(InputStream inputStream, Notification notification):
            execution(* *..*.*(..)) && !within(aspects.NotificationAspect) && args(inputStream)
            && @annotation(notification);

    after(InputStream inputStream, Notification notification) returning(Object ret): sendNotification(inputStream, notification){
        Response response = (Response) ret;
        if (response.getStatus() == 200){
            List<ConnectionInfoMsg> administrators = ServerMain.getInstance().getAdministrators();
            for(ConnectionInfoMsg connectionInfoMsg : administrators)
                new Thread(new RunnableNotification(connectionInfoMsg, notification.text())).start();
        }
    }
}
