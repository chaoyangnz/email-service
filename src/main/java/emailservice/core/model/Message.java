package emailservice.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * The view object representing a Email model when retrieving.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@Accessors(chain = true)
public class Message {

    @NotNull
    @Size(min = 1)
    private List<Recipient> to;

    private List<Recipient> cc;

    private List<Recipient> bcc;

    @NotNull
    @Size(max = 200)
    private String subject;

    @NotNull
    private Body body;
}
