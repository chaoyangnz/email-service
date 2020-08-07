package emailservice;

import emailservice.core.model.Body;
import emailservice.core.model.BodyType;
import emailservice.core.model.Message;
import emailservice.core.model.ProcessRecord;
import emailservice.core.model.ProcessState;
import emailservice.core.model.Recipient;
import emailservice.dataprovider.database.ProcessRecordJpaDataProvider;
import emailservice.dataprovider.database.entity.ProcessRecordEntity;
import emailservice.dataprovider.database.ProcessRecordRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;
import java.time.Instant;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcessRecordJpaDataProviderTest {
    //UUT
    private ProcessRecordJpaDataProvider processRecordJpaDataProvider;

    // dependencies
    private boolean enableDataPrivacy;
    @Mock
    private ProcessRecordRepository processRecordRepository;

    // mocks
    private Message message;
    private ProcessRecord processRecord;
    private ProcessRecordEntity processRecordEntity;

    @Captor
    private ArgumentCaptor<ProcessRecordEntity> processRecordEntityCaptor;

    @Before
    public void setup() {
        processRecordJpaDataProvider = new ProcessRecordJpaDataProvider(enableDataPrivacy, processRecordRepository);

        message = new Message()
            .setSubject("subject")
            .setTo(Arrays.asList(
                new Recipient().setName("user").setEmail("aa.bb@example.com")
            ))
            .setBody(
                new Body().setType(BodyType.HTML).setContent("<div>how is today?</div>")
            );
        Instant now = Instant.now();
        processRecord = new ProcessRecord()
            .setMessage(message)
            .setState(ProcessState.TRANSFORMED)
            .setCreatedAt(now);
        processRecordEntity = new ProcessRecordEntity()
            .setId(1L)
            .setMessage(message)
            .setState(ProcessState.TRANSFORMED)
            .setCreatedAt(now);
    }

    @Test
    public void given_repository_create_successfully_then_process_entity_is_populated() {
        //given
        when(processRecordRepository.save(processRecordEntityCaptor.capture())).thenReturn(processRecordEntity);
        //when
        ProcessRecord savedProcessRecord = processRecordJpaDataProvider.save(processRecord);
        //then
        assertThat(savedProcessRecord.getId()).isEqualTo(processRecordEntity.getId());
        assertThat(savedProcessRecord).isEqualToIgnoringGivenFields(processRecord, "id");
        assertThat(processRecordEntityCaptor.getValue()).isEqualToIgnoringGivenFields(processRecordEntity, "id");
    }

    @Test
    public void given_repository_update_successfully_then_process_entity_is_populated() {
        processRecord.setId(processRecordEntity.getId());
        //given
        when(processRecordRepository.save(processRecordEntityCaptor.capture())).thenReturn(processRecordEntity);
        //when
        ProcessRecord savedProcessRecord = processRecordJpaDataProvider.save(processRecord);
        //then
        assertThat(savedProcessRecord.getId()).isEqualTo(processRecordEntity.getId());
        assertThat(savedProcessRecord).isEqualToIgnoringGivenFields(processRecord, "id");
        assertThat(processRecordEntityCaptor.getValue()).isEqualToIgnoringGivenFields(processRecordEntity, "id");
    }

    @Test
    public void given_repository_save_with_error_then_exception_thrown() {
        processRecord.setId(processRecordEntity.getId());
        //given
        when(processRecordRepository.save(processRecordEntityCaptor.capture()))
            .thenThrow(Mockito.mock(DataAccessException.class));
        //when
        assertThatCode(() -> processRecordJpaDataProvider.save(processRecord))
            .isInstanceOf(DataAccessException.class);
    }

    @Test
    public void given_a_saved_ACCEPTED_record_then_should_not_update() {
        processRecord.setId(processRecordEntity.getId());
        processRecord.setState(ProcessState.ACCEPTED);
        //given
        //when
        ProcessRecord savedProcessRecord = processRecordJpaDataProvider.save(processRecord);
        //then
        assertThat(savedProcessRecord).isSameAs(processRecord);
        verify(processRecordRepository, never()).save(any());
    }
}
