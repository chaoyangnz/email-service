package emailservice.dataprovider;

import emailservice.core.dependencies.ProcessRecordDataProvider;
import emailservice.core.model.ProcessRecord;
import emailservice.dataprovider.entity.ProcessRecordEntity;
import emailservice.dataprovider.repository.ProcessRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProcessRecordJpaDataProvider implements ProcessRecordDataProvider {
    private final boolean enableDataPrivacy;
    private final ProcessRecordRepository processRecordRepository;

    public ProcessRecordJpaDataProvider(
        @Value("${emailservice.dataPrivacy.enable}") final boolean enableDataPrivacy,
        final ProcessRecordRepository processRecordRepository
    ) {
        this.enableDataPrivacy = enableDataPrivacy;
        this.processRecordRepository = processRecordRepository;
    }

    @Override
    public ProcessRecord save(ProcessRecord record) {
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
