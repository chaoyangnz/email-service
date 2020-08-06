import emailservice.core.dependencies.EnrichmentDataProvider;
import emailservice.core.model.Body;
import emailservice.core.model.BodyType;
import emailservice.usercase.HtmlBodyEnricher;
import emailservice.usercase.TextBodyEnricher;
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
public class TextBodyEnricherTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    //UUT
    private TextBodyEnricher textBodyEnricher;
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
            {"this is a test text", "what do you think", "this is a test text\n" +
                "what do you think", true},
            {"", "what do you think", "\n" +
                "what do you think", true},
        };
        return Arrays.asList(data);
    }

    @Before
    public void setup() {
        textBodyEnricher = new TextBodyEnricher(enrichmentDataProvider);
    }

    @Test
    public void testEnrichBody() {
        //given
        when(enrichmentDataProvider.getEnrichmentData()).thenReturn(enhancementData);
        Body body = new Body().setType(BodyType.TEXT).setContent(content);
        //when
        boolean enriched = textBodyEnricher.enrich(body);
        //then
        assertThat(enriched).isEqualTo(this.enriched);
        assertThat(body.getContent()).isEqualTo(enrichedContent);
    }

}
