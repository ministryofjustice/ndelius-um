package uk.co.bconline.ndelius.transformer;

import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.entity.DomainEventEntity;
import uk.co.bconline.ndelius.model.notification.DomainEvent;

import static java.util.Optional.ofNullable;

@Component
public class DomainEventTransformer
{
    public DomainEventEntity map(DomainEvent domainEvent)
    {
        return ofNullable(domainEvent)
                .map(de -> DomainEventEntity.builder()
                        .messageBody(de.getMessageBody())
                        .messageAttributes(de.getMessageAttributes())
                        .domainEventTypeId(de.getDomainEventTypeId())
                        .createdDateTime(de.getCreatedDateTime())
                        .failedPublishing(de.getFailedPublishing())
                        .build())
                .orElse(null);
    }

}
