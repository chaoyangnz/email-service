package emailservice.entrypoint;

import emailservice.core.model.Message;
import emailservice.core.model.Result;
import emailservice.usercase.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/email", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EmailEndpoint {

    private final EmailService emailService;

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Result> send(
        @RequestParam(name = "enrich", required = false, defaultValue = "false") boolean enrichBody,
        @Valid @RequestBody Message message) {
        return ResponseEntity.accepted().body(emailService.send(message, enrichBody));
    }
}
