package emailservice.usercase;

import com.fasterxml.jackson.databind.ObjectMapper;
import emailservice.core.dependencies.BodyEnricher;
import emailservice.core.dependencies.RecipientFilter;
import emailservice.core.dependencies.ProcessRecordDataProvider;
import emailservice.core.dependencies.EmailSender;
import emailservice.core.exception.ExternalAccessException;
import emailservice.core.model.ProcessState;
import emailservice.core.model.Message;
import emailservice.core.model.ProcessRecord;
import emailservice.core.model.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

@Service
public class EmailService {
    private final boolean enableFilter;

    private final EmailSender emailSender;
    private final List<BodyEnricher> bodyEnrichers;
    private final RecipientFilter recipientFilter;
    private final ProcessRecordDataProvider processRecordDataProvider;
    private final ObjectMapper objectMapper;

    public EmailService(
        @Value("${emailservice.recipientFilter.enable}") final boolean enableFilter,
        final EmailSender emailSender,
        final List<BodyEnricher> bodyEnrichers,
        final RecipientFilter recipientFilter,
        final ProcessRecordDataProvider processRecordDataProvider,
        final ObjectMapper objectMapper
    ) {
        this.enableFilter = enableFilter;
        this.emailSender = emailSender;
        this.bodyEnrichers = bodyEnrichers;
        this.recipientFilter = recipientFilter;
        this.processRecordDataProvider = processRecordDataProvider;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Result send(Message message, boolean enrichBody) {
        ProcessRecord processRecord = new ProcessRecord()
            .setOriginalMessage(message)
            .setState(ProcessState.ACCEPTED)
            .setCreatedAt(Instant.now());
        ProcessRecord savedProcessRecord = processRecordDataProvider.save(processRecord);

        if (enrichBody) {
            bodyEnrichers.stream().anyMatch(bodyEnricher -> bodyEnricher.enrich(message.getBody()));
        }
        if (enableFilter) {
            recipientFilter.filter(message.getTo());
            recipientFilter.filter(message.getCc());
            recipientFilter.filter(message.getBcc());
        }
        try {
            String messageId = emailSender.send(message);
            savedProcessRecord.setMessage(message).setExternalMessageId(messageId).setState(ProcessState.DISPATCHED);
            processRecordDataProvider.save(savedProcessRecord);

            return new Result().setId(savedProcessRecord.getId()).setSentAt(Instant.now());
        } catch (ExternalAccessException ex) {
            savedProcessRecord.setMessage(message).setState(ProcessState.DISPATCH_FAILED);
            processRecordDataProvider.save(savedProcessRecord);
            throw ex;
        }
    }
}
