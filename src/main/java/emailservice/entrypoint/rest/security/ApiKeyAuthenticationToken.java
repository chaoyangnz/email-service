package emailservice.entrypoint.rest.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

@RequiredArgsConstructor
public class ApiKeyAuthenticationToken implements Authentication {
    private final String token;
    @Getter @Setter
    private ApiKeyPrincipal principal;
    @Getter @Setter
    private boolean authenticated;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getCredentials() {
        return token;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public String getName() {
        return principal.getService();
    }
}
