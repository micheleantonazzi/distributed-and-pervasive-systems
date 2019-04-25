package server;



import messages.server.ConnectionInfoOuterClass.*;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import utility.ArrayListSynchronized;

import java.io.IOException;
import java.net.URI;

//Singleton
public class ServerMain {

    //STATIC VARIABLES
    private static final String BASE_URI = "http://localhost:11111/sdp-server/";

    private static ServerMain instance = null;

    //INSTANCE VARIABLES
    private ArrayListSynchronized<ConnectionInfo> administratorClientConnected = new ArrayListSynchronized<>();

    //Private constructor
    private ServerMain(){}

    public static ServerMain getInstance(){
        if (instance == null)
            instance = new ServerMain();
        return instance;
    }

    public static void main(String[] args) {
        final ResourceConfig resourceConfig = new ResourceConfig().packages("server");
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), resourceConfig);

        System.out.println(String.format("Server running at " + BASE_URI + "\nType to close..."));
        try{
            System.in.read();

        }
        catch (IOException ex){
            System.out.println(ex);
        }
        finally {
            System.out.println("Server shutdown");
            server.shutdownNow();
        }
    }

    public ArrayListSynchronized<ConnectionInfo> getAdministratorClientConnected(){
        return this.administratorClientConnected.clone();
    }

    public boolean addAdministratorClient(ConnectionInfo element){
        return this.administratorClientConnected.add(element);
    }
}
