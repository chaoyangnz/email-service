package emailservice.entrypoint.rest;

import emailservice.core.exception.AuthorizationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class ApiKeyAuthInterceptor extends HandlerInterceptorAdapter {

    @Value("${emailservice.security.apiKey}")
    private String apiKey;

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
        return this.apiKey.equals(apiKey);
    }
}
