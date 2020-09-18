package emailservice.entrypoint.rest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import emailservice.core.exception.AuthorizationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Service
public class ApiKeyAuthInterceptor extends HandlerInterceptorAdapter {
    @Value("${emailservice.apiKey.secret}")
    private final String apiKeySecret;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader("Authorization");
        boolean authorised = !authorizationHeaderIsInvalid(authorizationHeader) && apiKeyVerified(authorizationHeader);
        if(!authorised) {
            throw new AuthorizationException("Authorization is missing or invalid");
        }
        return true;
    }

    private boolean authorizationHeaderIsInvalid(String authorizationHeader) {
        return authorizationHeader == null
            || !authorizationHeader.startsWith("Bearer ");
    }

    private boolean apiKeyVerified(String authorizationHeader) {
        String apiKey = authorizationHeader.replace("Bearer ", "");
        try {
            Algorithm algorithm = Algorithm.HMAC256(apiKeySecret);
            JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("es.yang.to")
                .build();
            DecodedJWT jwt = verifier.verify(apiKey);
            return true;
        } catch (JWTVerificationException exception){
            //Invalid signature/claims
            return false;
        }
    }
}
