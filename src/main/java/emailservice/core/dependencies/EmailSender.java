package emailservice.core.dependencies;

import emailservice.core.model.Message;

public interface EmailSender {
    /**
     * send email via an email vendor.
     *
     * @param message
     * @return message ID from external vendor.
     */
    String send(Message message);
}
