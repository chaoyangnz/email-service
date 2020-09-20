package emailservice.entrypoint.rest.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ApiKeyPrincipal {
    // which service is the key representing
    private final String service;
    // which service account is delegated with this api key
    private final String serviceAccountId;
}
