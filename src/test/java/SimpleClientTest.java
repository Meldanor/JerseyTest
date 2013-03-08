import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

public class SimpleClientTest {

    @Test
    public void test() {
        try {
            // Start http server
            HttpServer server = HttpServerFactory.create("http://meldanor.dyndns.org:8080/rest");
            server.start();

            // Access method - Should return "yeah"
            String answer = Client.create().resource("http://localhost:8080/rest/message").get(String.class);
            System.out.println(answer);

            server.stop(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
