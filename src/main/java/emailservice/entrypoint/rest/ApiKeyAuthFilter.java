package emailservice.entrypoint.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Simple Auth filter with API key verification.
 */
@Service
@RequiredArgsConstructor
public class ApiKeyAuthFilter implements Filter {
    @Value("${emailservice.security.apiKey}")
    private String apiKey;

    @Override
    public void doFilter(
    ServletRequest request,
    ServletResponse response,
    FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String authorizationHeader = req.getHeader("Authorization");
        boolean authorised = !authorizationHeaderIsInvalid(authorizationHeader) && apiKeyVerified(authorizationHeader);
        if(!authorised) {
            res.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        chain.doFilter(request, response);
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