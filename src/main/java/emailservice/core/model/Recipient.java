package emailservice.core.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Accessors(chain = true)
public class Recipient {
    public static final String EMAIL_PATTERN = "^([a-zA-Z0-9_\\-\\.]+)@(([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5}))$";

    private String name;
    @NotBlank
    @Pattern(regexp = EMAIL_PATTERN)
    private String email;
}
