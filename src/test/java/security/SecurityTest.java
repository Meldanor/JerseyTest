package security;

import static org.junit.Assert.fail;
import manager.AccountManager;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import security.filter.LoginFilter;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

public class SecurityTest {

    private final static String BASE_URL = "http://localhost:8080/rest";

    private static HttpServer httpServer;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // Start Http Server
        httpServer = HttpServerFactory.create(BASE_URL);
        httpServer.start();
        System.out.println("Http server started");

        // Add test accounts using SHA512 hasing
        System.out.println("Add one test account");
        AccountManager aManager = AccountManager.getInstance();
        aManager.addUser("kilian", DigestUtils.sha512Hex("password"));
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        httpServer.stop(0);
        System.out.println("Http server stoped");
    }

    @Test
    public void test() {
        // Create REST client
        Client client = Client.create();
        WebResource tokenRes = client.resource(BASE_URL + "/account/recToken");
        // add a filter using the user and password to the resource
        tokenRes.addFilter(new LoginFilter("kilian", "password"));

        // access the resource and get the generated token
        Token token = tokenRes.get(Token.class);
        System.out.println(token);

        // Test with wrong values
        try {
            tokenRes.removeAllFilters();
            tokenRes.addFilter(new LoginFilter("kilian", "passwor"));
            // Generate exception!
            token = tokenRes.get(Token.class);
            System.out.println(token);
        } catch (UniformInterfaceException e) {
            // Password is wrong, so the status should be 401!
            if (!e.getMessage().equals("GET http://localhost:8080/rest/account/recToken returned a response status of 401 Unauthorized"))
                fail();
        }

    }
}
