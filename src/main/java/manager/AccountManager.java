package manager;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.codec.binary.Hex;

import security.Token;

import com.sun.jersey.core.util.Base64;

public class AccountManager {

    // Singelton pattern
    private static AccountManager INSTANCE;

    public static AccountManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new AccountManager();
        return INSTANCE;
    }

    // Placeholder for database
    private Map<String, SaltedPassword> userMap;

    private AccountManager() {
        this.userMap = new HashMap<String, SaltedPassword>();
        this.tokenByUserMap = new HashMap<String, Token>();
        this.tokenByIDMap = new HashMap<String, Token>();
    }

    public static final class SaltedPassword {
        // Use Strings instead of the charArray because strings are immutable,
        // arrays aren't.
        final String hash;
        final String salt;
        final int iterations;

        public SaltedPassword(String hash, String salt, int iterations) {
            this.hash = hash;
            this.salt = salt;
            this.iterations = iterations;
        }

        public final boolean isEquals(SaltedPassword other) {
            return this.hash.equals(other.hash);
        }

        public byte[] getHash() {
            try {
                return Hex.decodeHex(hash.toCharArray());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public byte[] getSalt() {
            try {
                return Hex.decodeHex(salt.toCharArray());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public String toString() {
            return String.format("%d:%s:%s", iterations, hash, salt);
        }
    }

    public boolean addUser(String username, String preHash) {
        try {
            char[] password = preHash.toCharArray();
            int saltLength = Hex.decodeHex(password).length;
            byte[] salt = generateSalt(saltLength);
            SaltedPassword pw = saltPassword(password, salt, saltLength);
            userMap.put(username, pw);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean validateUser(HttpHeaders headers) {
        MultivaluedMap<String, String> headMap = headers.getRequestHeaders();
        // extract Value behind Authorization head from request
        String base64String = headMap.getFirst(HttpHeaders.AUTHORIZATION);
        // Not authorization head in the request
        if (base64String == null)
            return false;
        // decode base64 string
//        System.out.println(base64String);
        base64String = base64String.substring("Basic ".length());
//        System.out.println(base64String);
        base64String = Base64.base64Decode(base64String);
//        System.out.println(base64String);
        // Split at ':' as defined for HTTPBasicAuthentification
        int pos = base64String.indexOf(':');
        // Not found
        if (pos == -1)
            return false;
        // extract values
        String username = base64String.substring(0, pos);
        String password = base64String.substring(pos + 1);

//        System.out.println(username);
//        System.out.println(password);
        return validateUser(username, password);
    }

    public String extractUser(HttpHeaders header) {
        // TODO: All conversion shit to another utility class
        String base64String = header.getRequestHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        // ERROR
        if (base64String == null)
            return null;
        base64String = base64String.substring("Basic ".length());
        base64String = Base64.base64Decode(base64String);

        String user = base64String.substring(0, base64String.indexOf(':'));
        return user;
    }

    public boolean validateUser(String username, String password) {
        try {
            SaltedPassword origin = userMap.get(username);
            if (origin == null)
                return false;
            byte[] salt = origin.getSalt();
            SaltedPassword generated = saltPassword(password.toCharArray(), salt, origin.iterations, salt.length);
            return origin.isEquals(generated);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private byte[] generateSalt(int saltLength) {
        SecureRandom rand = new SecureRandom();
        byte[] salt = new byte[saltLength];
        rand.nextBytes(salt);
        return salt;
    }

    private final static String KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
    private final static int DEFAULT_ITERATION = 1000;

    private SaltedPassword saltPassword(char[] password, byte[] salt, int bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return saltPassword(password, salt, DEFAULT_ITERATION, bytes);
    }

    private SaltedPassword saltPassword(char[] password, byte[] salt, int iterations, int bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        String saltString = Hex.encodeHexString(salt);
        String saltedHash = Hex.encodeHexString(skf.generateSecret(spec).getEncoded());
        return new SaltedPassword(saltedHash, saltString, iterations);
    }

    // ************
    // ** TOKENS **
    // ************

    // Token Map
    private Map<String, Token> tokenByUserMap;
    private Map<String, Token> tokenByIDMap;

    private final static long TOKEN_EXPIRATION = TimeUnit.HOURS.toMillis(1L);

    public Token generateToken(String username) {

        // Generate random uudids
        UUID tokenID = UUID.randomUUID();
        UUID tokenSecret = UUID.randomUUID();
        
        // generate token
        Token token = new Token(username, tokenID.toString(), tokenSecret.toString(), TOKEN_EXPIRATION);
        this.tokenByUserMap.put(username.toLowerCase(), token);
        this.tokenByIDMap.put(token.getKey(), token);
        return token;
    }

    public Token removeToken(String username) {
        return this.tokenByUserMap.remove(username.toLowerCase());
    }

    public Token getTokenByUser(String username) {
        return tokenByUserMap.get(username.toLowerCase());
    }

    public Token getTokenByID(String tokenId) {
        return tokenByIDMap.get(tokenId);
    }
}
