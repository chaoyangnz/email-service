package emailservice.entrypoint.rest;

import emailservice.core.exception.AuthorizationException;
import emailservice.core.exception.EmailSenderException;
import emailservice.core.exception.EnrichmentDataAccessException;
import emailservice.core.exception.RecipientRequiredException;
import emailservice.core.model.GenericError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    protected GenericError handleApiAuthorizationException(Exception ex) {
        GenericError errorResponse = new GenericError()
            .setMessage(ex.getMessage());
        log.info("unauthorized request", ex);
        return errorResponse;
    }

    @ExceptionHandler(RecipientRequiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected GenericError handleRecipientRequiredException(Exception ex) {
        GenericError errorResponse = new GenericError()
            .setMessage(ex.getMessage());
        log.info("empty recipient", ex);
        return errorResponse;
    }

    @ExceptionHandler({ EmailSenderException.class, EnrichmentDataAccessException.class, DataAccessException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    protected GenericError handleException(Exception ex) {
        GenericError errorResponse = new GenericError()
            .setMessage(ex.getMessage());
        log.error("internal server error", ex);
        return errorResponse;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        GenericError errorResponse = new GenericError().
                setMessage("Validation failed");
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errorResponse.getErrors().put(fieldName, errorMessage);
        });
        log.info("input validation failed", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


}



