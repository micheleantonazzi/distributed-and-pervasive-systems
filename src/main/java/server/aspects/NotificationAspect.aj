package server.aspects;

import server.aspects.annotations.Notification;
import messages.AdministratorInfoMsgOuterClass.AdministratorInfoMsg;
import server.Administrators;
import server.threads.RunnableNotification;
import javax.ws.rs.core.Response;
import java.util.Set;


//This aspect sends automatically notifications
public aspect NotificationAspect {

    pointcut sendNotification(Notification notification):
            execution(* *..*.*(..)) && !within(server.aspects.NotificationAspect)
            && @annotation(notification);

    after(Notification notification) returning(Object ret): sendNotification(notification){
        Response response = (Response) ret;
        if(response.getStatus() == 200){
            Set<AdministratorInfoMsg> administrators = Administrators.getInstance().getSet();
            for(AdministratorInfoMsg connectionInfoMsg : administrators)
                new Thread(new RunnableNotification(connectionInfoMsg, notification.text())).start();
        }
    }
}
