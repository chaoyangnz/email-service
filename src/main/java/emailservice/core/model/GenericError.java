package emailservice.core.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class GenericError {
    private int statusCode;
    private String message;
    private Map<String, String> validationDetails = new HashMap<>();
}
