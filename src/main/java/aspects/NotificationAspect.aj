package aspects;

import aspects.annotations.Notification;
import messages.AdministratorInfoMsgOuterClass.AdministratorInfoMsg;
import server.ServerMain;
import server.threads.RunnableNotification;
import javax.ws.rs.core.Response;
import java.util.Set;


//This aspect sends automatically notifications
public aspect NotificationAspect {

    pointcut sendNotification(Notification notification):
            execution(* *..*.*(..)) && !within(aspects.NotificationAspect)
            && @annotation(notification);

    after(Notification notification) returning(Object ret): sendNotification(notification){
        Response response = (Response) ret;
        if(response.getStatus() == 200){
            Set<AdministratorInfoMsg> administrators = ServerMain.getInstance().getAdministrators();
            for(AdministratorInfoMsg connectionInfoMsg : administrators)
                new Thread(new RunnableNotification(connectionInfoMsg, notification.text())).start();
        }
    }
}
