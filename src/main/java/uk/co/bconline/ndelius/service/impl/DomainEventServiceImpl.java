package uk.co.bconline.ndelius.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.entity.NDParameterEntity;
import uk.co.bconline.ndelius.model.entity.ReferenceDataEntity;
import uk.co.bconline.ndelius.model.notification.DomainEvent;
import uk.co.bconline.ndelius.model.notification.HmppsDomainEvent;
import uk.co.bconline.ndelius.model.notification.HmppsDomainEventType;
import uk.co.bconline.ndelius.repository.db.DomainEventRepository;
import uk.co.bconline.ndelius.repository.db.NDParameterRepository;
import uk.co.bconline.ndelius.repository.db.ReferenceDataRepository;
import uk.co.bconline.ndelius.service.DomainEventService;
import uk.co.bconline.ndelius.transformer.DomainEventTransformer;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Service
public class DomainEventServiceImpl implements DomainEventService
{
    private final ReferenceDataRepository referenceDataRepository;

    private final DomainEventRepository domainEventRepository;
    private final DomainEventTransformer domainEventTransformer;

    private final NDParameterRepository ndParameterRepository;

    private static final String ENABLE_DOMAIN_EVENTS_PARAM = "ENABLE_DOMAIN_EVENTS";
    private static final String SELF_PUBLISH_DOMAIN_EVENTS_PARAM = "SELF_PUBLISH_DOMAIN_EVENTS";
    private static final String DOMAIN_EVENT_TYPE_REF_DATA_CODE_SET = "DOMAIN EVENT TYPE";

    @Autowired
    public DomainEventServiceImpl(
            ReferenceDataRepository referenceDataRepository,
            DomainEventRepository domainEventRepository,
            DomainEventTransformer domainEventTransformer,
            NDParameterRepository ndParameterRepository) {
        this.referenceDataRepository = referenceDataRepository;
        this.domainEventRepository = domainEventRepository;
        this.domainEventTransformer = domainEventTransformer;
        this.ndParameterRepository = ndParameterRepository;
    }

    @Override
    public void insertDomainEvent(HmppsDomainEventType eventType, HashMap<String, String> attributes)
    {
        if (isDomainEventEnabled())
        {
            DomainEvent domainEvent = createDomainEvent(eventType, attributes);
            if (domainEvent != null)
            {
                domainEventRepository.save(domainEventTransformer.map(domainEvent));
            } else
            {
                log.debug("Domain event flags are enabled but no domain event could be created for event type: {}", eventType.getEventType());
            }
        }
    }

    private DomainEvent createDomainEvent(HmppsDomainEventType eventType, HashMap<String, String> eventAttributes)
    {
        try
        {
            Optional<ReferenceDataEntity> domainEventType = referenceDataRepository.findByCodeAndReferenceDataMasterCodeSetName(eventType.getEventType(), DOMAIN_EVENT_TYPE_REF_DATA_CODE_SET);
            if (domainEventType.isEmpty())
            {
                log.debug("Reference data for domain event type {} not found", eventType.getEventType());
                return null;
            }

            HmppsDomainEvent domainEventMessage = HmppsDomainEvent.builder()
                    .eventType(eventType.getEventType())
                    .description(eventType.getEventDescription())
                    .occurredAt(ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
                    .additionalInformation(eventAttributes)
                    .version(1)
                    .build();

            ObjectMapper jsonMapper = new ObjectMapper();

            HashMap<String, HashMap<String, String>> attributes = new HashMap<>();
            HashMap<String, String> attributeValues = new HashMap<>();

            attributeValues.put("Type", "String");
            attributeValues.put("Value", eventType.getEventType());
            attributes.put("eventType", attributeValues);

            return DomainEvent.builder()
                    .domainEventTypeId(domainEventType.get().getId())
                    .messageBody(jsonMapper.writeValueAsString(domainEventMessage))
                    .messageAttributes(jsonMapper.writeValueAsString(attributes))
                    .createdDateTime(LocalDateTime.now())
                    .build();
        } catch (JsonProcessingException e)
        {
            log.error("Error creating domain event", e);
            return null;
        }
    }

    private boolean isDomainEventEnabled()
    {
        Optional<NDParameterEntity> enableDomainEventsFlag = ndParameterRepository.findByNdParameter(ENABLE_DOMAIN_EVENTS_PARAM);
        Optional<NDParameterEntity> selfPublishFlag = ndParameterRepository.findByNdParameter(SELF_PUBLISH_DOMAIN_EVENTS_PARAM);

        // For a message to be saved to the DOMAIN_EVENTS table, the following conditions must be met:
        // 1. The ENABLE_DOMAIN_EVENTS ND parameter must be set to 1
        // 2. The SELF_PUBLISH_DOMAIN_EVENTS ND parameter must be set to 0
        return (enableDomainEventsFlag.isPresent() && enableDomainEventsFlag.get().getNdValue().equals(1d)
                && selfPublishFlag.isPresent() && selfPublishFlag.get().getNdValue().equals(0d));
    }
}
