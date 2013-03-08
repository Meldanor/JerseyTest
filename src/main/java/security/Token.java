package security;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "token")
public class Token {

    @XmlElement(name = "key")
    private final String key;
    @XmlElement(name = "secret")
    private final String secret;
    @XmlElement(name = "owner")
    private final String owner;
    @XmlElement(name = "expiration")
    private final long expiration;

    public Token() {
        key = null;
        secret = null;
        owner = null;
        expiration = 0L;
    }

    public Token(final String key, final String secret, final String owner, final long expireDuration) {
        this.key = key;
        this.secret = secret;
        this.owner = owner;
        this.expiration = expireDuration + System.currentTimeMillis();
    }

    public String getKey() {
        return key;
    }

    public String getSecret() {
        return secret;
    }

    public String getOwner() {
        return owner;
    }

    public boolean isExpired() {
        return expiration <= System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return String.format("Token={Key: %s; Secret: %s; Owner: %s; Expiration: %d}", key, secret, owner, expiration);
    }
}
