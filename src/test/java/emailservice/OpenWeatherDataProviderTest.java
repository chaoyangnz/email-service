package emailservice;

import emailservice.core.exception.ExternalAccessException;
import emailservice.dataprovider.network.OpenWeatherDataProvider;
import emailservice.dataprovider.network.dto.OpenWeatherData;
import emailservice.dataprovider.network.dto.OpenWeatherResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OpenWeatherDataProviderTest {

    // UUT
    private OpenWeatherDataProvider openWeatherDataProvider;

    // dependencies
    @Mock
    private RestTemplate restTemplate;

    private String location;
    private String apiKey;
    private String endpoint;

    // mocks
    private OpenWeatherResponse openWeatherResponse;
    @Mock
    private ResponseEntity<OpenWeatherResponse> responseResponseEntity;

    // captors
    @Captor
    private ArgumentCaptor<String> endpointCaptor;
    @Captor
    private ArgumentCaptor<Map<String, String>> uriVariablesCaptor;

    @Before
    public void setup() {
        openWeatherResponse = new OpenWeatherResponse().setMain(
            new OpenWeatherData()
            .setTemp(12.99)
        );
        location = "Auckland";
        apiKey = "whatever secret";
        endpoint = "https://api.openweathermap.org/data/2.5/weather?q={location}&units=metric&appid={apiKey}";
        openWeatherDataProvider = new OpenWeatherDataProvider(location, apiKey, endpoint, restTemplate);
    }

    @Test
    public void given_openweather_responds_data_then_retrieve_successfully() {
        //given
        when(responseResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(responseResponseEntity.getBody()).thenReturn(openWeatherResponse);
        when(restTemplate.getForEntity(endpointCaptor.capture(), any(Class.class), uriVariablesCaptor.capture())).
            thenReturn(responseResponseEntity);
        //when
        String data = openWeatherDataProvider.getEnrichmentData();
        //then
        assertThat(data).isEqualTo("Temperature in Auckland: 12.99 °C");
        assertThat(endpointCaptor.getValue()).isEqualTo(endpoint);
        assertThat(uriVariablesCaptor.getValue()).contains(
            entry("location", location), entry("apiKey", apiKey)
        );
    }

    @Test
    public void given_openweather_responds_with_error_then_retrieve_failed() {
        //given
        when(responseResponseEntity.getStatusCode()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);
        when(responseResponseEntity.getBody()).thenReturn(null);
        when(restTemplate.getForEntity(endpointCaptor.capture(), any(Class.class), uriVariablesCaptor.capture())).
            thenReturn(responseResponseEntity);
        //when
        assertThatCode(() -> openWeatherDataProvider.getEnrichmentData())
            .isInstanceOf(ExternalAccessException.class)
            .hasMessage("Weather not found")
            .hasNoCause();

        //then
        assertThat(endpointCaptor.getValue()).isEqualTo(endpoint);
        assertThat(uriVariablesCaptor.getValue()).contains(
            entry("location", location), entry("apiKey", apiKey)
        );
    }
}
