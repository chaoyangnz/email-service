package emailservice.entrypoint.rest.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import emailservice.core.model.GenericError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityErrorHandler extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (AuthenticationException authenticationException) {
            sendError(response, HttpStatus.UNAUTHORIZED.value(), authenticationException.getMessage());
        } catch (AccessDeniedException accessDeniedException) {
            sendError(response, HttpStatus.FORBIDDEN.value(), accessDeniedException.getMessage());
        }
    }

    private void sendError(HttpServletResponse response, int code, String message) throws IOException {
        GenericError errorResponse = new GenericError().setStatusCode(code).setMessage(message);
        ObjectMapper mapper = new ObjectMapper();
        response.setStatus(code);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().println(mapper.writeValueAsString(errorResponse));
    }
}
