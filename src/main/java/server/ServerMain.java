package server;



import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class ServerMain {

    private static final String BASE_URI = "http://localhost:11111/sdp-server/";

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
}
