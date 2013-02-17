import org.junit.Test;

import com.sun.jersey.api.client.Client;

public class SimpleClientTest {

    @Test
    public void test() {
        try {
            String l = Client.create().resource("http://localhost:8080/rest").get(String.class);
            System.out.println(l);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
