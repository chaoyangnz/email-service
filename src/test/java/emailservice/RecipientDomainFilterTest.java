package emailservice;

import emailservice.core.model.Recipient;
import emailservice.core.usercase.RecipientDomainFilter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class RecipientDomainFilterTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    //UUT
    private RecipientDomainFilter recipientDomainFilter;

    @Parameterized.Parameter(0)
    public String email;
    @Parameterized.Parameter(1)
    public boolean filtered;
    @Parameterized.Parameter(2)
    public String domain;

    @Parameterized.Parameters(name = "{index}: email={0} is filtered out ? {1} for domain={2}")
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] {
            {"a@aa.com", true, "example.com"},
            {"a@example.com", true, "example.co.nz"},
            {"abcd.xyz11@example.com", false, "example.com"},
            {"abcd.xyz11@example.com.nz", true, "example.com"},
            {"abcd-xx@raken.com", true, "raken.app"},
        };
        return Arrays.asList(data);
    }

    @Before
    public void setup() {
        recipientDomainFilter = new RecipientDomainFilter(domain);
    }

    @Test
    public void testFilter() {
        //given
        List<Recipient> recipients = new ArrayList<>();
        recipients.add(
            new Recipient().setEmail(email)
        );
        //when
        recipientDomainFilter.filter(recipients);
        //then
        assertThat(recipients.isEmpty()).isEqualTo(filtered);
    }
}
