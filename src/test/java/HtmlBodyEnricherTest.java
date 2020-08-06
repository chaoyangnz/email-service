import emailservice.core.dependencies.EnrichmentDataProvider;
import emailservice.core.model.Body;
import emailservice.core.model.BodyType;
import emailservice.usercase.HtmlBodyEnricher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import java.util.Arrays;
import java.util.Collection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class HtmlBodyEnricherTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    //UUT
    private HtmlBodyEnricher htmlBodyEnricher;
    //dependencies
    @Mock
    private EnrichmentDataProvider enrichmentDataProvider;

    @Parameterized.Parameter(0)
    public String content;
    @Parameterized.Parameter(1)
    public String enhancementData;
    @Parameterized.Parameter(2)
    public String enrichedContent;
    @Parameterized.Parameter(3)
    public boolean enriched;

    @Parameterized.Parameters(name = "{index}: body={0} is enriched with fragment={1} as {2} with ? {3}")
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] {
            {"<p>hello</p>", "<quote>happy Friday</quote>", "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                "  <p>hello</p>\n" +
                "  <p>\n" +
                "   <quote>\n" +
                "    happy Friday\n" +
                "   </quote></p>\n" +
                " </body>\n" +
                "</html>", true},
            {"<html><body>hello</body></html>", "<quote>happy Friday</quote>", "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                "  hello\n" +
                "  <p>\n" +
                "   <quote>\n" +
                "    happy Friday\n" +
                "   </quote></p>\n" +
                " </body>\n" +
                "</html>", true},
        };
        return Arrays.asList(data);
    }

    @Before
    public void setup() {
        htmlBodyEnricher = new HtmlBodyEnricher(enrichmentDataProvider);
    }

    @Test
    public void testEnrichBody() {
        //given
        when(enrichmentDataProvider.getEnrichmentData()).thenReturn(enhancementData);
        Body body = new Body().setType(BodyType.HTML).setContent(content);
        //when
        boolean enriched = htmlBodyEnricher.enrich(body);
        //then
        assertThat(enriched).isEqualTo(this.enriched);
        assertThat(body.getContent()).isEqualTo(enrichedContent);
    }

}
