package emailservice.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Accessors(chain = true)
public class Body {
    @NotNull
    private BodyType type;
    @NotBlank
    @Size(max = 5000)
    private String content;
}
