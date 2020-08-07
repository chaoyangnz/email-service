package emailservice.dataprovider.database.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import emailservice.core.model.ProcessState;
import emailservice.core.model.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "email_record")
@Getter @Setter
@Accessors(chain = true)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ProcessRecordEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Type(type = "jsonb")
    @Column(name = "original_message", columnDefinition = "jsonb")
    private Message originalMessage;

    @Type(type = "jsonb")
    @Column(name = "message", columnDefinition = "jsonb")
    private Message message;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "external_message_id")
    private String externalMessageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private ProcessState state;
}
