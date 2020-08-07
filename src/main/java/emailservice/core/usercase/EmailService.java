package emailservice.core.usercase;

import com.rits.cloning.Cloner;
import emailservice.core.exception.EmailSenderException;
import emailservice.core.exception.RecipientRequiredException;
import emailservice.core.model.Message;
import emailservice.core.model.ProcessRecord;
import emailservice.core.model.ProcessState;
import emailservice.core.model.Recipient;
import emailservice.core.model.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
public class EmailService {
    private boolean enableFilter;

    private final EmailSender emailSender;
    private final List<BodyEnricher> bodyEnrichers;
    private final RecipientFilter recipientFilter;
    private final ProcessRecordDataProvider processRecordDataProvider;

    public EmailService(
        @Value("${emailservice.recipientFilter.enable}") final boolean enableFilter,
        final EmailSender emailSender,
        final List<BodyEnricher> bodyEnrichers,
        final RecipientFilter recipientFilter,
        final ProcessRecordDataProvider processRecordDataProvider
        ) {
        this.enableFilter = enableFilter;
        this.emailSender = emailSender;
        this.bodyEnrichers = bodyEnrichers;
        this.recipientFilter = recipientFilter;
        this.processRecordDataProvider = processRecordDataProvider;
    }

    public Result send(Message message, boolean enrichBody) {
        ProcessRecord processRecord = new ProcessRecord().setCreatedAt(Instant.now())
            .setOriginalMessage(snapshot(message))
            .setState(ProcessState.ACCEPTED);
        ProcessRecord saveProcessRecord = processRecordDataProvider.save(processRecord);

        try {
            return doSend(message, enrichBody, saveProcessRecord);
        } catch (EmailSenderException ex) {
            saveProcessRecord.setState(ProcessState.DISPATCH_FAILED);
            throw ex;
        } finally {
            processRecordDataProvider.save(saveProcessRecord);
        }
    }

    private Result doSend(Message message, boolean enrichBody, ProcessRecord processRecord) {
        applyBodyEnrichment(enrichBody, message);
        applyRecipientFilter(message);
        processRecord.setMessage(snapshot(message)).setState(ProcessState.TRANSFORMED);

        String messageId = emailSender.send(message);
        processRecord.setExternalMessageId(messageId).setState(ProcessState.DISPATCHED);
        return new Result().setId(processRecord.getId()).setSentAt(Instant.now());
    }

    private void applyBodyEnrichment(final boolean enrichBody, final Message message) {
        if (enrichBody) {
            bodyEnrichers.stream().anyMatch(bodyEnricher -> bodyEnricher.enrich(message.getBody()));
        }
    }

    private void applyRecipientFilter(final Message message) {
        if (enableFilter) {
            recipientFilter.filter(message.getTo());
            recipientFilter.filter(message.getCc());
            recipientFilter.filter(message.getBcc());
        }

        checkToRecipient(message);
    }

    private void checkToRecipient(final Message message) {
        if (message.getTo().isEmpty()) {
            throw new RecipientRequiredException("No recipient is provided or email domain is not whitelisted");
        }
    }

    private Message snapshot(Message message) {
        return new Cloner().deepClone(message);
    }
}
