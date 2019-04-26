package server.rest;

import messages.server.ConnectionInfoMsgOuterClass.*;
import server.ServerMain;
import server.threads.RunnableNotification;

import java.io.InputStream;
import java.util.List;

public aspect NotificationAspect {

    pointcut sendNotification(InputStream inputStream, server.Notification notification):
            execution(* *..*.*(..)) && !within(NotificationAspect) && args(inputStream)
            && @annotation(notification);

    after(InputStream inputStream, server.Notification notification): sendNotification(inputStream, notification){
        List<ConnectionInfoMsg> administrators = ServerMain.getInstance().getAdministrators();
        for(ConnectionInfoMsg connectionInfoMsg : administrators){
            new Thread(new RunnableNotification(connectionInfoMsg, notification.text())).start();
        }

    }
}
