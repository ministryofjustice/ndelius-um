package uk.co.bconline.ndelius.model.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomainEvent
{
    private Long id;
    private String messageBody;
    private String messageAttributes;
    private Long domainEventTypeId;
    private LocalDateTime createdDateTime;
    private Boolean failedPublishing;
}
