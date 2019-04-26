package server.threads;

import messages.administrator.NotificationMsgOuterClass.*;
import messages.server.ConnectionInfoMsgOuterClass.*;
import server.Notification;
import server.ServerMain;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class RunnableNotification implements Runnable {
    private ConnectionInfoMsg connectionInfo;
    String text;

    public RunnableNotification(ConnectionInfoMsg connectionInfo, String text){
        this.connectionInfo = connectionInfo;
        this.text = text;
    }

    @Override
    public void run() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://" + connectionInfo.getAddress() + ":" + connectionInfo.getPort());

        target.path("administrator/notification").request().post(
                Entity.entity(NotificationMsg.newBuilder().setText(text).build().toByteArray(),
                        MediaType.APPLICATION_OCTET_STREAM));
    }
}
