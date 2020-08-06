package emailservice.core.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
