package emailservice.dataprovider.network;

import emailservice.core.exception.EnrichmentDataAccessException;
import emailservice.core.usercase.EnrichmentDataProvider;
import emailservice.dataprovider.network.dto.OpenWeatherData;
import emailservice.dataprovider.network.dto.OpenWeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
@Service
public class OpenWeatherDataProvider implements EnrichmentDataProvider {
    @Value("${emailservice.openweather.location}")
    private final String location;
    @Value("${emailservice.openweather.apiKey}")
    private final String apiKey;
    @Value("${emailservice.openweather.endpoint.currentWeather}")
    private final String endpoint;
    private final RestTemplate restTemplate;

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
