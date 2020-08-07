package emailservice.dataprovider.network;

import emailservice.core.exception.EnrichmentDataAccessException;
import emailservice.core.usercase.EnrichmentDataProvider;
import emailservice.dataprovider.network.dto.OpenWeatherData;
import emailservice.dataprovider.network.dto.OpenWeatherResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import static java.lang.String.format;

@Service
@Slf4j
public class OpenWeatherDataProvider implements EnrichmentDataProvider {
    private final String location;
    private final String apiKey;
    private final String endpoint;
    private final RestTemplate restTemplate;

    public OpenWeatherDataProvider(
        @Value("${emailservice.openweather.location}") final String location,
        @Value("${emailservice.openweather.apiKey}") final String apiKey,
        @Value("${emailservice.openweather.endpoint.currentWeather}") final String endpoint,
        final RestTemplate restTemplate
    ) {
        this.location = location;
        this.apiKey = apiKey;
        this.endpoint = endpoint;
        this.restTemplate = restTemplate;
    }

    @Override
    public String getEnrichmentData() {
        OpenWeatherData data = request().getMain();
        return format("Temperature in %s: %s °C", location, data.getTemp());
    }

    private OpenWeatherResponse request() {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("location", location);
        uriVariables.put("apiKey", apiKey);
        ResponseEntity<OpenWeatherResponse> responseEntity =
            restTemplate.getForEntity(endpoint, OpenWeatherResponse.class, uriVariables);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        throw new EnrichmentDataAccessException("Weather retrieval errored", null);
    }
}
