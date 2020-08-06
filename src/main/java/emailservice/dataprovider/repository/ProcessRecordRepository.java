package emailservice.dataprovider.repository;

import emailservice.dataprovider.entity.ProcessRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessRecordRepository extends JpaRepository<ProcessRecordEntity, Long> {
}
