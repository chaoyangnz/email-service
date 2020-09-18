package emailservice.dataprovider.database;

import emailservice.core.model.ProcessState;
import emailservice.core.usercase.ProcessRecordDataProvider;
import emailservice.core.model.ProcessRecord;
import emailservice.dataprovider.database.entity.ProcessRecordEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class ProcessRecordJpaDataProvider implements ProcessRecordDataProvider {
    @Value("${emailservice.dataPrivacy.enable}")
    private final boolean enableDataPrivacy;
    private final ProcessRecordRepository processRecordRepository;

    @Override
    public ProcessRecord save(ProcessRecord record) {
        // never update an initial state.
        if (record.getId() != null && record.getState() == ProcessState.ACCEPTED) {
            return record;
        }
        ProcessRecordEntity processRecordEntity = build(record);
        if (!enableDataPrivacy) {
            processRecordEntity.setMessage(record.getMessage());
            processRecordEntity.setOriginalMessage(record.getOriginalMessage());
        }
        processRecordEntity.setExternalMessageId(record.getExternalMessageId());
        ProcessRecordEntity savedProcessRecordEntity = processRecordRepository.save(processRecordEntity);

        return build(savedProcessRecordEntity);
    }

    private ProcessRecordEntity build(ProcessRecord processRecord) {
        return new ProcessRecordEntity()
            .setId(processRecord.getId())
            .setCreatedAt(processRecord.getCreatedAt())
            .setExternalMessageId(processRecord.getExternalMessageId())
            .setMessage(processRecord.getMessage())
            .setOriginalMessage(processRecord.getOriginalMessage())
            .setState(processRecord.getState());
    }

    private ProcessRecord build(ProcessRecordEntity processRecordEntity) {
        return new ProcessRecord()
            .setId(processRecordEntity.getId())
            .setCreatedAt(processRecordEntity.getCreatedAt())
            .setExternalMessageId(processRecordEntity.getExternalMessageId())
            .setMessage(processRecordEntity.getMessage())
            .setOriginalMessage(processRecordEntity.getOriginalMessage())
            .setState(processRecordEntity.getState());
    }
}
