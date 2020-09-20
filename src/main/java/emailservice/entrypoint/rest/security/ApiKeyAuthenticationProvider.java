package emailservice.entrypoint.rest.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import java.util.Map;

@RequiredArgsConstructor
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {
    private final String apiKeySecret;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ApiKeyAuthenticationToken apiKeyAuthenticationToken = (ApiKeyAuthenticationToken) authentication;
        return verify(apiKeyAuthenticationToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private ApiKeyAuthenticationToken verify(ApiKeyAuthenticationToken apiKeyAuthenticationToken) {
        String apiKey = apiKeyAuthenticationToken.getCredentials();
        try {
            Algorithm algorithm = Algorithm.HMAC256(apiKeySecret);
            JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("bitsflux.com")
                .build();
            DecodedJWT jwt = verifier.verify(apiKey);
            Map<String, Claim> claims = jwt.getClaims();
            apiKeyAuthenticationToken.setPrincipal(
                new ApiKeyPrincipal(jwt.getAudience().get(0), jwt.getSubject())
            );
            // TODO: check expire
            apiKeyAuthenticationToken.setAuthenticated(true);
            return apiKeyAuthenticationToken;
        } catch (JWTVerificationException exception){
            //Invalid signature/claims
            return null;
        }
    }
}
