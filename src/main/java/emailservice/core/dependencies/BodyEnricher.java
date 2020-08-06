package emailservice.core.dependencies;

import emailservice.core.model.Body;

public interface BodyEnricher {
    boolean enrich(Body body);
}
