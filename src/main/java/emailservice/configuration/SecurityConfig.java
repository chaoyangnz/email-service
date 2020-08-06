//package emailservice.configuration;
//
//import emailservice.entrypoint.ApiExceptionHandler;
//import emailservice.entrypoint.security.ApiKeyAuthFilter;
//import emailservice.entrypoint.security.ApiKeyAuthenticationManager;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@RequiredArgsConstructor
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Value("${emailservice.security.apiKey}")
//    private String apiKey;
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        ApiKeyAuthFilter filter = new ApiKeyAuthFilter();
//        filter.setAuthenticationManager(new AuthenticationManager() {
//
//            @Override
//            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//                String principal = (String) authentication.getPrincipal();
//                if (!apiKey.equals(principal))
//                {
//                    throw new BadCredentialsException("The API key was not found or not the expected value.");
//                }
//                authentication.setAuthenticated(true);
//                return authentication;
//            }
//        });
//        http.
//            antMatcher("/email").
//            csrf().disable().
//            sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
//            and().addFilter(filter).authorizeRequests().anyRequest().authenticated();
//    }
//
//}