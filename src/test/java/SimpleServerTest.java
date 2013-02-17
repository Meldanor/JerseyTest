import javax.swing.JOptionPane;

import org.junit.Test;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

public class SimpleServerTest {

    @Test
    public void test() {
        try {
            HttpServer server = HttpServerFactory.create("http://meldanor.dyndns.org:8080/rest");
            server.start();
            JOptionPane.showMessageDialog(null, "Stop server");
            server.stop(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
