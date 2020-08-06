package emailservice.entrypoint;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class GenericErrorResponse {
    private String message;
    private Map<String, String> errors = new HashMap<>();
}
