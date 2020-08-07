package emailservice;

import emailservice.core.exception.EmailSenderException;
import emailservice.core.model.Body;
import emailservice.core.model.BodyType;
import emailservice.core.model.Message;
import emailservice.core.model.ProcessRecord;
import emailservice.core.model.ProcessState;
import emailservice.core.model.Recipient;
import emailservice.core.model.Result;
import emailservice.core.usercase.BodyEnricher;
import emailservice.core.usercase.EmailSender;
import emailservice.core.usercase.EmailService;
import emailservice.core.usercase.ProcessRecordDataProvider;
import emailservice.core.usercase.RecipientFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.byLessThan;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmailServiceTest {
    //UUT
    private EmailService emailService;
    //dependencies
    private boolean enableFilter = true;
    @Mock
    private EmailSender emailSender;
    @Mock
    private BodyEnricher textBodyEnricher;
    @Mock
    private BodyEnricher htmlBodyEnricher;
    @Mock
    private BodyEnricher anotherHtmlBodyEnricher;
    private List<BodyEnricher> bodyEnrichers;
    @Mock
    private RecipientFilter recipientFilter;
    @Mock
    private ProcessRecordDataProvider processRecordDataProvider;
    // mocks
    private Message message;
    private ProcessRecord processRecord;
    private String messageId;
    private Long id;

    // captors
    @Captor
    private ArgumentCaptor<Body> bodyArgumentCaptor;
    @Captor
    private ArgumentCaptor<List<Recipient>> recipientsCaptor;
    @Captor
    private ArgumentCaptor<ProcessRecord> processRecordCaptor;
    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    @Before
    public void setup() {
        bodyEnrichers = Arrays.asList(htmlBodyEnricher, textBodyEnricher, anotherHtmlBodyEnricher);
        emailService = new EmailService(enableFilter, emailSender, bodyEnrichers, recipientFilter, processRecordDataProvider);
        message = new Message()
            .setSubject("email subject")
            .setTo(Arrays.asList(
                new Recipient().setName("send to").setEmail("to@example.com")
            ))
            .setBody(
                new Body().setType(BodyType.HTML).setContent("<div>email body</div>")
            );
        id = 1L;
        processRecord = new ProcessRecord()
            .setId(id);
        messageId = "whatever-x-message-id-from-email-sender";
    }

    @Test
    public void given_email_sender_send_successfully_and_filter_enabled__and_require_enrich_then_should_filter_and_enrich_message() {
        //given
        when(htmlBodyEnricher.enrich(bodyArgumentCaptor.capture())).thenReturn(true);
//        doNothing().when(recipientFilter).filter(recipientsCaptor.capture());
        when(processRecordDataProvider.save(processRecordCaptor.capture())).thenReturn(processRecord);
        when(emailSender.send(messageCaptor.capture())).thenReturn(messageId);
        //when
        Result result = emailService.send(message, true);
        //then
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getSentAt()).isCloseTo(Instant.now(), byLessThan(1, ChronoUnit.MINUTES));
        assertThat(processRecord.getState()).isEqualTo(ProcessState.DISPATCHED);
        verify(emailSender, times(1)).send(any(Message.class));
        verify(htmlBodyEnricher, times(1)).enrich(any(Body.class));
        verify(textBodyEnricher, never()).enrich(any(Body.class));
        verify(anotherHtmlBodyEnricher, never()).enrich(any(Body.class));
        verify(recipientFilter).filter(anyList());
        verify(processRecordDataProvider, times(2)).save(any(ProcessRecord.class));
    }

    @Test
    public void given_email_sender_send_successfully_and_filter_enabled__and_not_require_enrich_then_should_filter_and_not_enrich_message() {
        //given
//        when(htmlBodyEnricher.enrich(bodyArgumentCaptor.capture())).thenReturn(true);
//        doNothing().when(recipientFilter).filter(recipientsCaptor.capture());
        when(processRecordDataProvider.save(processRecordCaptor.capture())).thenReturn(processRecord);
        when(emailSender.send(messageCaptor.capture())).thenReturn(messageId);
        //when
        Result result = emailService.send(message, false);
        //then
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getSentAt()).isCloseTo(Instant.now(), byLessThan(1, ChronoUnit.MINUTES));
        assertThat(processRecord.getState()).isEqualTo(ProcessState.DISPATCHED);
        verify(emailSender, times(1)).send(any(Message.class));
        verify(htmlBodyEnricher, never()).enrich(any(Body.class));
        verify(textBodyEnricher, never()).enrich(any(Body.class));
        verify(anotherHtmlBodyEnricher, never()).enrich(any(Body.class));
        verify(recipientFilter).filter(anyList());
        verify(processRecordDataProvider, times(2)).save(any(ProcessRecord.class));
    }

    @Test
    public void given_email_sender_send_failed_and_filter_enabled__and_require_enrich_then_should_throw_exception() {
        //given
        when(htmlBodyEnricher.enrich(bodyArgumentCaptor.capture())).thenReturn(true);
//        doNothing().when(recipientFilter).filter(recipientsCaptor.capture());
        when(processRecordDataProvider.save(processRecordCaptor.capture())).thenReturn(processRecord);
        when(emailSender.send(messageCaptor.capture())).thenThrow(EmailSenderException.class);
        //when
        assertThatCode(() -> emailService.send(message, true))
            .isInstanceOf(EmailSenderException.class);
        //then
        assertThat(processRecord.getState()).isEqualTo(ProcessState.DISPATCH_FAILED);
        verify(emailSender, times(1)).send(any(Message.class));
        verify(htmlBodyEnricher, times(1)).enrich(any(Body.class));
        verify(textBodyEnricher, never()).enrich(any(Body.class));
        verify(anotherHtmlBodyEnricher, never()).enrich(any(Body.class));
        verify(recipientFilter).filter(anyList());
        verify(processRecordDataProvider, times(2)).save(any(ProcessRecord.class));
    }

}
