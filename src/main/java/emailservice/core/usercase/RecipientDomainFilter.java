package emailservice.core.usercase;

import emailservice.core.model.Recipient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.lang.String.format;

/**
 * Filter out emails with domain not in the whitelist.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RecipientDomainFilter implements RecipientFilter {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(Recipient.EMAIL_PATTERN);

    @Value("${emailservice.recipientFilter.domain}")
    private final String whitelistDomain;

    @Override
    public void filter(List<Recipient> recipients) {
        if(recipients == null) return;
        recipients.removeIf(recipient -> {
            boolean notMatched = !matchWhitelistEmailDomain(recipient.getEmail());
            if (notMatched) {
                log.info(format("Filtered out recipient: %s due to not matching %s domain name",
                    recipient.getEmail(), whitelistDomain));
            }
            return notMatched;
        });
    }

    private boolean matchWhitelistEmailDomain(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        if (matcher.matches()) {
            String domain = matcher.group(2);
            return whitelistDomain.equals(domain);
        }
        return false;
    }
}
