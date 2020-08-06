package emailservice.core.dependencies;

import emailservice.core.model.ProcessRecord;

public interface ProcessRecordDataProvider {
    /**
     * Save email processing record entry.
     *
     * @param record email process record entry.
     * @return saved entry.
     */
    ProcessRecord save(ProcessRecord record);
}
