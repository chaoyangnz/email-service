package emailservice.core.dependencies;

public interface EnrichmentDataProvider {
    /**
     * get data to enrich email message.
     *
     * @return formatted data.
     */
    String getEnrichmentData();
}
