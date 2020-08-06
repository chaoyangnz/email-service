package emailservice.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BodyType {
    TEXT("text/plain"),
    HTML("text/html");

    private String mime;
}
