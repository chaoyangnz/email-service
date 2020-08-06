package emailservice.dataprovider.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class OpenWeatherResponse {
    private OpenWeatherData main;
}
