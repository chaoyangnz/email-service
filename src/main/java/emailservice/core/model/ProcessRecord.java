package emailservice.core.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.time.Instant;

@Getter
@Setter
@Accessors(chain = true)
public class ProcessRecord {
    private Long id;
    private String externalMessageId;
    private Message originalMessage;
    private Message message;
    private ProcessState state;
    private Instant createdAt;
}
