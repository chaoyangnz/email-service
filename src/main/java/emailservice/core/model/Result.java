package emailservice.core.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.Instant;

@Getter
@Setter
@Accessors(chain = true)
public class Result {
    @NotNull
    @Null(groups = ValidationGroup.Creation.class)
    private Long id;

    @NotNull
    @Null(groups = ValidationGroup.Creation.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant sentAt;
}
