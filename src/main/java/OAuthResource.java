import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.oauth.server.OAuthServerRequest;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;
import com.sun.jersey.oauth.signature.OAuthSignature;
import com.sun.jersey.oauth.signature.OAuthSignatureException;

@Path("/oauth")
public class OAuthResource {

    // Test values
    private static Map<String, String> keys;

    static {
        keys = new HashMap<String, String>();
        keys.put("dpf43f3p2l4k3l03", "kd94hf93k423kf44");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String handle(@Context HttpContext hc) {

        // Parse the request
        OAuthServerRequest osr = new OAuthServerRequest(hc.getRequest());

        // Extract the parameter for authentification from the request
        OAuthParameters params = new OAuthParameters().readRequest(osr);
        // Extract the consumer key to get the assigned consumer secret
        String consumerKey = keys.get(params.get(OAuthParameters.CONSUMER_KEY));
        // Consumer key is an invalid key - consumer does not exists!
        if (consumerKey == null)
            return Boolean.FALSE.toString();

        // Genereate the secret by using the consumer key and the consumer
        // secret
        OAuthSecrets secrets = new OAuthSecrets().consumerSecret(consumerKey);
        consumerKey = null;
        System.gc();

        try {
            // Verify
            return Boolean.toString(OAuthSignature.verify(osr, params, secrets));
        } catch (OAuthSignatureException ose) {
            return Boolean.FALSE.toString();
        }
    }
}
