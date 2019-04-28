package aspects;

import aspects.annotations.Notification;
import messages.server.ConnectionInfoMsgOuterClass.*;
import server.ServerMain;
import server.threads.RunnableNotification;
import java.io.InputStream;
import java.util.List;


//This aspect sends automatically notifications
public aspect NotificationAspect {

    pointcut sendNotification(InputStream inputStream, Notification notification):
            execution(* *..*.*(..)) && !within(aspects.NotificationAspect) && args(inputStream)
            && @annotation(notification);

    /*void around(InputStream inputStream, Notification notification): sendNotification(inputStream, notification){
        try{
            proceed(inputStream, notification);

            //if the method is completed correctly, the notification is sent
            List<ConnectionInfoMsg> administrators = ServerMain.getInstance().getAdministrators();
            for(ConnectionInfoMsg connectionInfoMsg : administrators){
                new Thread(new RunnableNotification(connectionInfoMsg, notification.text())).start();
            }
            System.out.println("house added");
        }
        catch (Exception exception){
            System.out.println("Error parsing stream\n" + exception);
        }
    }
    */

}
