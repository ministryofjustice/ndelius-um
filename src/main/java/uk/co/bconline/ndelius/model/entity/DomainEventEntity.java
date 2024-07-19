package uk.co.bconline.ndelius.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DOMAIN_EVENT", schema = "delius_app_schema")
public class DomainEventEntity
{
    @Id
    @Column(name = "DOMAIN_EVENT_ID")
    @SequenceGenerator(name = "DOMAIN_EVENT_ID_SEQ", sequenceName = "DOMAIN_EVENT_ID_SEQ", allocationSize = 1)
    @GeneratedValue(generator = "DOMAIN_EVENT_ID_SEQ")
    private Long id;

    @Column(name = "MESSAGE_BODY")
    private String messageBody;

    @Column(name = "MESSAGE_ATTRIBUTES")
    private String messageAttributes;

    @ManyToOne()
    @JoinColumn(name = "DOMAIN_EVENT_TYPE_ID", insertable = false, updatable = false)
    private ReferenceDataEntity domainEventType;

    @Column(name = "DOMAIN_EVENT_TYPE_ID")
    private Long domainEventTypeId;

    @Column(name = "CREATED_DATETIME")
    private LocalDateTime createdDateTime;

    @Column(name = "FAILED_PUBLISHING")
    private Boolean failedPublishing;
}
