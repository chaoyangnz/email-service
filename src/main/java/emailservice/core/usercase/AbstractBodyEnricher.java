package emailservice.core.usercase;

import emailservice.core.exception.EnrichmentDataAccessException;
import emailservice.core.model.Body;
import emailservice.core.model.BodyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract body enricher providing some template methods.
 */
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractBodyEnricher implements BodyEnricher {
    protected final EnrichmentDataProvider enrichmentDataProvider;

    @Override
    public boolean enrich(Body body) {
        if (!support(body.getType())) return false;
        String content = body.getContent();
        try {
            String data = enrichmentDataProvider.getEnrichmentData();
            body.setContent(doEnrich(content, data));
            return true;
        } catch (EnrichmentDataAccessException ex) {
            log.warn(ex.getMessage(), ex);
            // non-critical, continue the flow
            return false;
        }
    }

    protected abstract boolean support(BodyType type);
    protected abstract String doEnrich(String content, String data);
}
