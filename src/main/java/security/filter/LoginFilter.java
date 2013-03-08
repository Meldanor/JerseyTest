package security.filter;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.codec.digest.DigestUtils;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.core.util.Base64;

public class LoginFilter extends ClientFilter {

    // Base64 encoded string for Basic Http Authentifaction
    private final String authentication;

    public LoginFilter(String user, String password) {
        authentication = createAuthentificationString(user, password);
    }

    // Using SHA512 for creating a hexadecimal encoded hash
    private static final int HASH_PASSWORD_LENGTH = 64;

    private String createAuthentificationString(String user, String password) {

        // Authentifaction string as defined in HTTP for Basic
        // Authentification(User:Password in Base64)

        // Build string
        StringBuilder sBuilder = new StringBuilder(user.length() + HASH_PASSWORD_LENGTH);
        sBuilder.append(user).append(':');
        // Hash password with sha512
        sBuilder.append(DigestUtils.sha512Hex(password));
        System.out.println(sBuilder);
        try {
            // Encode the string
            return "Basic " + new String(Base64.encode(sBuilder.toString()), "ASCII");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Override
    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {

        // Append authentifaction string to the http request
        if (!cr.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            cr.getHeaders().add(HttpHeaders.AUTHORIZATION, authentication);
        }
        return getNext().handle(cr);
    }

}
