package emailservice.core.usercase;

import emailservice.core.model.BodyType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import static java.lang.String.format;

/**
 * Enricher for html email body.
 */
@Service
@Order(2)
public class HtmlBodyEnricher extends AbstractBodyEnricher {

    public HtmlBodyEnricher(EnrichmentDataProvider enrichmentDataProvider) {
        super(enrichmentDataProvider);
    }

    @Override
    protected boolean support(BodyType type) {
        return type == BodyType.HTML;
    }

    @Override
    protected String doEnrich(String content, String data) {
        if(data == null) {
            return content;
        }
        Document doc = Jsoup.parse(content);
        doc.body().append(format("<p>%s</p>", data));
        return doc.toString();
    }
}
