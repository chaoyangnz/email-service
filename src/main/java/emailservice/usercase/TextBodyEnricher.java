package emailservice.usercase;

import emailservice.core.dependencies.EnrichmentDataProvider;
import emailservice.core.model.BodyType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import static java.lang.String.format;

/**
 * Enricher for text email body.
 */
@Service
@Order(1)
public class TextBodyEnricher extends AbstractBodyEnricher {

    public TextBodyEnricher(EnrichmentDataProvider enrichmentDataProvider) {
        super(enrichmentDataProvider);
    }

    @Override
    protected boolean support(BodyType type) {
        return type == BodyType.TEXT;
    }

    @Override
    protected String doEnrich(String content, String data) {
        return format("%s\n%s", content, data);
    }
}
