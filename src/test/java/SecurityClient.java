import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.HMAC_SHA1;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;

public class SecurityClient {

    @Test
    public void authentificate() {
        System.out.print("Login with correct values: ");
        assertTrue("fail", login("dpf43f3p2l4k3l03", "kd94hf93k423kf44"));
        System.out.println(" OK");
        System.out.print("Login with false values: ");
        assertFalse("fail", login("dpf43f3p2l4k3l03", "whuhrghrguhrgeuh"));
        System.out.println(" OK");
    }

    public boolean login(String userName, String password) {
        String base = "http://localhost:8080/rest";

        Client client = Client.create();

        // Create parameter for authentification - the user name and using HMAC
        // to verify the user
        OAuthParameters params = new OAuthParameters().consumerKey(userName).signatureMethod(HMAC_SHA1.NAME);
        // Create the secret including the users password
        OAuthSecrets secrets = new OAuthSecrets().consumerSecret(password);

        // Filter for the web resource - filter can be used for more methods and
        // must not generated so often
        OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(), params, secrets);

        // Try to login
        WebResource resource = client.resource(base + "/login");
        resource.addFilter(filter);

        String response = resource.get(String.class);
        return Boolean.parseBoolean(response);
    }
}
