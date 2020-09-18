package emailservice.configuration;

import com.sendgrid.SendGrid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import javax.validation.Validation;
import javax.validation.Validator;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class AppConfig implements WebMvcConfigurer {

    @Value("${emailservice.sendgrid.apiKey}")
    private final String sendGridApiKey;

    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(sendGridApiKey);
    }

    @Bean
    public Validator validator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }
}