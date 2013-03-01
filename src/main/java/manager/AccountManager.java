package manager;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Hex;

public class AccountManager {

    // Singelton pattern
    private static AccountManager INSTANCE;

    public static AccountManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new AccountManager();
        return INSTANCE;
    }

    private Map<String, SaltedPassword> userMap;

    private AccountManager() {
        this.userMap = new HashMap<String, SaltedPassword>();
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

}
