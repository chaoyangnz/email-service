package emailservice.core.usercase;

import emailservice.core.model.Body;

public interface BodyEnricher {
    boolean enrich(Body body);
}
