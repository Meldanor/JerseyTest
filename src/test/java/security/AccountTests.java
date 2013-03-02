package security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import manager.AccountManager;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

public class AccountTests {

    @Test
    public void createAccount() {
        System.out.println("Adding accounts to the system");
        AccountManager aManager = AccountManager.getInstance();
        assertTrue(aManager.addUser("Kilian", Hex.encodeHexString("1q2w3e4r".getBytes())));
        assertTrue(aManager.addUser("Matze", Hex.encodeHexString("1q2w3e4r".getBytes())));
        assertTrue(aManager.addUser("Tabea", Hex.encodeHexString("1q2w3e4r".getBytes())));
        assertTrue(aManager.addUser("Luigi", Hex.encodeHexString("1q2w3e4r".getBytes())));
        assertTrue(aManager.addUser("Markus", Hex.encodeHexString("1q2w3e4r".getBytes())));
        System.out.println("Accounts added");
    }

    @Test
    public void verifiyAccounts() {
        AccountManager aManager = AccountManager.getInstance();
        System.out.print("Login with User Kilian and his correct password:");
        assertTrue(aManager.validateUser("Kilian", Hex.encodeHexString("1q2w3e4r".getBytes())));
        System.out.println(" OK");
        System.out.print("Login with User Kilian and his wrong password:");
        assertFalse(aManager.validateUser("Kilian", Hex.encodeHexString("1q2w3e4".getBytes())));
        System.out.println(" OK");
    }

    @Test
    public void RESTAPI() {
        // Start Server
        HttpServer server = null;
        try {
            server = HttpServerFactory.create("http://meldanor.dyndns.org:8080/rest");

        } catch (Exception e) {
            e.printStackTrace();
        }
        server.start();
        
        // Create client
        String base = "http://localhost:8080/rest/account";
        Client client = Client.create();
        
        // Create new account
        WebResource create = client.resource(base + "/create");
        ClientResponse response = create.path("Mario").path(Hex.encodeHexString("Mamamia123".getBytes())).put(ClientResponse.class);
        System.out.println("Create Account: " + response.getClientResponseStatus());

        // Login with the account(correct values)
        WebResource verify = client.resource(base + "/verify");
        response = verify.path("Mario").path(Hex.encodeHexString("Mamamia123".getBytes())).get(ClientResponse.class);
        System.out.println("Login with correct values: " + response.getClientResponseStatus());
        
        // Login with the account(wrong values)
        response = verify.path("Mario").path(Hex.encodeHexString("mamamia123".getBytes())).get(ClientResponse.class);
        System.out.println("Login with wrong values: " + response.getClientResponseStatus());
        server.stop(0);
    }

}
