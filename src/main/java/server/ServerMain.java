package server;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

//Singleton
public class ServerMain {

    //STATIC VARIABLES
    //address of main server
    public static final String SERVER_ADDRESS = "localhost";
    public static final int SERVER_PORT = 11111;
    public static final String SERVER_URI = "http://" + SERVER_ADDRESS + ":" + SERVER_PORT + "/server/";

    private static ServerMain instance = null;

    //Private constructor
    private ServerMain(){}

    public static synchronized ServerMain getInstance(){
        if (instance == null)
            instance = new ServerMain();
        return instance;
    }

    public static void main(String[] args) {
        final ResourceConfig resourceConfig = new ResourceConfig().packages("server.rest");
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(SERVER_URI), resourceConfig);
        System.out.println(String.format("Server running at " + SERVER_URI + "\n"));
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
}
