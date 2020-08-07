import emailservice.EmailServiceApplication;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = EmailServiceApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class EmailServiceIntegrationTest {

    @LocalServerPort
    private int port;

    private Header authHeader;

    private List<Object[]> valid;
    private List<Object[]> invalid;

    @Before
    public void setup() {
        RestAssured.port = port;
        RestAssured.basePath = "/";
        authHeader = new Header("Authorization", "Bearer foo");
        valid = Arrays.asList(new Object[][] {
            {"message.json", 202},
            {"message_full.json", 202},
            {"message_unknown_fields.json", 202},
        });
        invalid = Arrays.asList(new Object[][] {
            {"message_empty.json", 400},
            {"message_empty_subject.json", 400},
            {"message_empty_to.json", 400},
            {"message_empty_body.json", 400},
            {"message_invalid_body_type.json", 400},
            {"message_too_long_subject.json", 400},
            {"message_too_long_body.json", 400},
            {"message_empty_to_after_filter.json", 400},
        });
    }

    private String payload(String file) {
        File resource = null;
        try {
            resource = new ClassPathResource(file).getFile();
            return new String(Files.readAllBytes(resource.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void unauthorized() {
        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            body(payload("message.json")).
        when().
            post("email").
        then().
            statusCode(401);
    }

    @Test
    public void notAcceptableContentType() {
        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.TEXT_PLAIN_VALUE).
            header(authHeader).
            body(payload("message.json")).
        when().
            post("email").
        then().
            statusCode(406);
    }

    @Test
    public void success() {
        valid.forEach((entry) -> {
            String input = (String) entry[0];
            int statusCode = (Integer) entry[1];
            given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                header(authHeader).
                body(payload(input)).
            when().
                post("email").
            then().
                statusCode(202).
                body("id", greaterThan(0));
        });
    }

    @Test
    public void invalidInput() {
        invalid.forEach((entry) -> {
            String input = (String) entry[0];
            int statusCode = (Integer) entry[1];
            given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                header(authHeader).
                body(payload(input)).
            when().
                post("email").
            then().
                statusCode(statusCode).
                body("message", notNullValue()).
                body("errors", notNullValue());
        });
    }

}
