package emailservice.dataprovider;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import emailservice.core.dependencies.EmailSender;
import emailservice.core.exception.ExternalAccessException;
import emailservice.core.model.Recipient;
import emailservice.core.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SendGridEmailSender implements EmailSender {
    private final static String MESSAGE_ID_HEADER = "X-Message-Id";

    private final String fromEmail;
    private final String fromName;
    private final String sendGridSendEndpoint;

    private final SendGrid sendGrid;

    public SendGridEmailSender(
        @Value("${emailservice.from.email}") final String fromEmail,
        @Value("${emailservice.from.name}") final String fromName,
        @Value("${emailservice.sendgrid.endpoint.send}") final String sendGridSendEndpoint,
        final SendGrid sendGrid
    ) {
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.sendGridSendEndpoint = sendGridSendEndpoint;
        this.sendGrid = sendGrid;
    }

    @Override
    public String send(Message message) {
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint(sendGridSendEndpoint);

        try {
            request.setBody(this.build(message).build());
            Response response = sendGrid.api(request);
            log.debug(String.valueOf(response.getStatusCode()));
            log.debug(response.getBody());
            log.debug(response.getHeaders().toString());
            return response.getHeaders().get(MESSAGE_ID_HEADER);
        } catch (IOException ex) {
            // wrap the exception to our application exception
            throw new ExternalAccessException(ExternalAccessException.API_SENDGRID, ex.getMessage(), ex);
        }
    }

    private Mail build(Message message) {
        Personalization personalization = new Personalization();
        this.buildRecipients(message.getTo()).stream().forEach(personalization::addTo);
        if (message.getCc() != null) {
            this.buildRecipients(message.getCc()).stream().forEach(personalization::addCc);
        }
        if (message.getBcc() != null) {
            this.buildRecipients(message.getBcc()).stream().forEach(personalization::addBcc);
        }
        Mail mail = new Mail();
        mail.setFrom(new Email(fromEmail, fromName));
        mail.addPersonalization(personalization);
        mail.setSubject(message.getSubject());
        mail.addContent(new Content(message.getBody().getType().getMime(), message.getBody().getContent()));

        return mail;
    }

    private List<Email> buildRecipients(List<Recipient> recipients) {
        return recipients.stream().map(recipient -> new Email(recipient.getEmail(), recipient.getName())).collect(Collectors.toList());
    }
}
