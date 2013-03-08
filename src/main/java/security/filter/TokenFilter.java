package security.filter;

import security.Token;

public class TokenFilter extends LoginFilter {

    public TokenFilter(Token token) {
        super(token.getKey(), token.getSecret());
    }

}
