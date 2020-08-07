package emailservice.core.usercase;

import emailservice.core.model.Recipient;
import java.util.List;

public interface RecipientFilter {
    /**
     * filter out recipients as per rules.
     *
     * @param recipients a list of recipients.
     */
    void filter(List<Recipient> recipients);
}
