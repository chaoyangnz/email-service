package emailservice.configuration;

import emailservice.entrypoint.rest.security.ApiKeyAuthenticationFilter;
import emailservice.entrypoint.rest.security.ApiKeyAuthenticationProvider;
import emailservice.entrypoint.rest.security.SecurityErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;

@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${emailservice.apiKey.secret}")
    private final String apiKeySecret;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
            "/error",
            "/actuator/**",
            "/static/**",
            "/webjars/**",
            "/swagger-ui.html**",
            "/swagger-resources/**",
            "/favicon.ico");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/api/**")
                    .authenticated()
            .and()
            .addFilterBefore(new SecurityErrorHandler(), WebAsyncManagerIntegrationFilter.class)
            .addFilterBefore(new ApiKeyAuthenticationFilter(this.authenticationManager()), RequestCacheAwareFilter.class)
            // disable SessionManagementFilter
            .sessionManagement().disable()
            // disable AnonymousAuthenticationFilter
            .anonymous().disable()
            // disable ExceptionTranslationFilter
            .exceptionHandling().disable()
            // disable LogoutFilter
            .logout().disable()
            // disable CsrfFilter
            .csrf().disable();;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new ApiKeyAuthenticationProvider(apiKeySecret));
    }

}
