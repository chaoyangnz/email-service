import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import emailservice.core.exception.ExternalAccessException;
import emailservice.core.model.Body;
import emailservice.core.model.BodyType;
import emailservice.core.model.Message;
import emailservice.core.model.Recipient;
import emailservice.dataprovider.SendGridEmailSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SendGridEmailSenderTest {
    //UUT
    private SendGridEmailSender sendGridEmailSender;
    //dependencies
    private String fromEmail;
    private String fromName;
    private String sendGridSendEndpoint;
    @Mock
    private SendGrid sendGrid;
    // mocks
    private Message message;
    private String sendGridMessageId;
    private Response response;
    //captors
    @Captor
    private ArgumentCaptor<Request> requestCaptor;

    @Before
    public void setup() throws Exception {
        fromEmail = "from@example.com";
        fromName = "from user";
        sendGridSendEndpoint = "/email/send";
        sendGridEmailSender = new SendGridEmailSender(fromEmail, fromName, sendGridSendEndpoint, sendGrid);
        message = new Message()
            .setSubject("subject")
            .setTo(Arrays.asList(
                new Recipient().setName("user").setEmail("aa.bb@example.com")
            ))
            .setBody(
                new Body().setType(BodyType.HTML).setContent("<div>how is today?</div>")
            );

        sendGridMessageId = "this is a message id from sendgrid response";
        response = new Response();
        response.setBody("");
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Message-Id", sendGridMessageId);
        response.setHeaders(headers);
    }

    @Test
    public void given_sendgrid_accepted_email_send_request_then_should_get_message_id() throws IOException {
        //given
        doReturn(response).when(sendGrid).api(requestCaptor.capture());
        //when
        String messageId = sendGridEmailSender.send(message);
        //then
        assertThat(messageId).isEqualTo(sendGridMessageId);
        Request request = requestCaptor.getValue();
        assertThat(request.getEndpoint()).isEqualTo(sendGridSendEndpoint);
        assertThat(request.getBody()).isEqualTo("{\"from\":{\"name\":\"from user\",\"email\":\"from@example.com\"},\"subject\":\"subject\",\"personalizations\":[{\"to\":[{\"name\":\"user\",\"email\":\"aa.bb@example.com\"}]}],\"content\":[{\"type\":\"text/html\",\"value\":\"<div>how is today?</div>\"}]}");
        assertThat(request.getMethod()).isEqualTo(Method.POST);
    }

    @Test
    public void given_sendgrid_respond_with_error_then_should_throw_exception() throws IOException {
        //given
        when(sendGrid.api(requestCaptor.capture())).thenThrow(IOException.class);
        //when & then
        assertThatCode(() -> sendGridEmailSender.send(message))
            .isInstanceOf(ExternalAccessException.class);

        Request request = requestCaptor.getValue();
        assertThat(request.getEndpoint()).isEqualTo(sendGridSendEndpoint);
        assertThat(request.getBody()).isEqualTo("{\"from\":{\"name\":\"from user\",\"email\":\"from@example.com\"},\"subject\":\"subject\",\"personalizations\":[{\"to\":[{\"name\":\"user\",\"email\":\"aa.bb@example.com\"}]}],\"content\":[{\"type\":\"text/html\",\"value\":\"<div>how is today?</div>\"}]}");
        assertThat(request.getMethod()).isEqualTo(Method.POST);
    }
}
