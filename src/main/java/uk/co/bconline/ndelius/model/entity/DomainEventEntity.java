package uk.co.bconline.ndelius.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DOMAIN_EVENT")
public class DomainEventEntity
{
    @Id
    @Column(name = "DOMAIN_EVENT_ID")
    @SequenceGenerator(name = "DOMAIN_EVENT_ID_SEQ", sequenceName = "DOMAIN_EVENT_ID_SEQ", allocationSize = 1)
    @GeneratedValue(generator = "DOMAIN_EVENT_ID_SEQ")
    private Long id;

    @Column(name = "MESSAGE_BODY")
    @Lob
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
