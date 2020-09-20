package emailservice.entrypoint.rest.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private final static String APIKEY_SCHEME = "Apikey ";

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeaderIsInvalid(authorizationHeader)) {
            throw new MissingCredentialsException("Authorization header is missing or not an 'Apikey' scheme");
        }
        Authentication authentication = new ApiKeyAuthenticationToken(authorizationHeader.replace("Bearer ", ""));
        authentication = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private boolean authorizationHeaderIsInvalid(String authorizationHeader) {
        return authorizationHeader == null
            || !authorizationHeader.startsWith(APIKEY_SCHEME);
    }

    public static class MissingCredentialsException extends AuthenticationException {
        public MissingCredentialsException(String msg) {
            super(msg);
        }
    }
}
