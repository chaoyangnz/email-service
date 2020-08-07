package emailservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BodyType {
    TEXT("text/plain"),
    HTML("text/html");

    private String mime;

    @JsonCreator
    public static BodyType forValue(String value) {
        try {
            return BodyType.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
