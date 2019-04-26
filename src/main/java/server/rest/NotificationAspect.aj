package server.rest;

import java.io.InputStream;

public aspect NotificationAspect {

    pointcut sendNotification(InputStream inputStream, server.Notification notification):
            execution(* *..*.*(..)) && !within(NotificationAspect) && args(inputStream)
            && @annotation(notification);

    after(InputStream inputStream, server.Notification notification): sendNotification(inputStream, notification){
        System.out.println(notification.text());
    }
}
