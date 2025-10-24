package uk.co.bconline.ndelius.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.entity.DomainEventEntity;
import uk.co.bconline.ndelius.model.notification.HmppsDomainEvent;
import uk.co.bconline.ndelius.model.notification.HmppsDomainEventType;
import uk.co.bconline.ndelius.repository.db.DomainEventRepository;
import uk.co.bconline.ndelius.repository.db.ReferenceDataRepository;
import uk.co.bconline.ndelius.service.DomainEventService;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
public class DomainEventServiceImpl implements DomainEventService {
    private final ReferenceDataRepository referenceDataRepository;

    private final DomainEventRepository domainEventRepository;

    private final ObjectMapper mapper;

    private static final String DOMAIN_EVENT_TYPE_REF_DATA_CODE_SET = "DOMAIN EVENT TYPE";

    @Autowired
    public DomainEventServiceImpl(
        ReferenceDataRepository referenceDataRepository,
        DomainEventRepository domainEventRepository,
        ObjectMapper mapper) {
        this.referenceDataRepository = referenceDataRepository;
        this.domainEventRepository = domainEventRepository;
        this.mapper = mapper;
    }

    @Override
    @SneakyThrows
    public void insertDomainEvent(HmppsDomainEventType eventType, Map<String, String> additionalInformation) {
        val type = referenceDataRepository.findByCodeAndReferenceDataMasterCodeSetName(eventType.getEventType(), DOMAIN_EVENT_TYPE_REF_DATA_CODE_SET)
            .orElseThrow(() -> new IllegalStateException("Reference data for domain event type " + eventType.getEventType() + " not found"));
        val message = HmppsDomainEvent.builder()
            .eventType(eventType.getEventType())
            .description(eventType.getEventDescription())
            .occurredAt(ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
            .additionalInformation(additionalInformation)
            .version(1)
            .build();
        val attributes = Map.of("eventType", Map.of("Type", "String", "Value", eventType.getEventType()));
        val entity = DomainEventEntity.builder()
            .messageBody(mapper.writeValueAsString(message))
            .messageAttributes(mapper.writeValueAsString(attributes))
            .domainEventTypeId(type.getId())
            .createdDateTime(LocalDateTime.now())
            .build();

        domainEventRepository.save(entity);
    }
}
