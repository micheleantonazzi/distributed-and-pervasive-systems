package server.rest;

import java.io.InputStream;

public aspect NotificationAspect {

    pointcut sendNotification(InputStream inputStream):
            execution(@server.Notification * *..*.*(..)) && !within(NotificationAspect) && args(inputStream);

    after(InputStream inputStream): sendNotification(inputStream){
        System.out.println("aspect");
        System.out.println(thisJoinPoint);
    }
}
