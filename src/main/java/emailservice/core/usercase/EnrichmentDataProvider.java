package emailservice.core.usercase;

public interface EnrichmentDataProvider {
    /**
     * get data to enrich email message.
     *
     * @return formatted data.
     */
    String getEnrichmentData();
}
