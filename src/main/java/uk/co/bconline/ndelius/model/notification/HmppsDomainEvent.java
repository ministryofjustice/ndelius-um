package uk.co.bconline.ndelius.model.notification;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HmppsDomainEvent
{
    private String eventType;
    private int version;
    private String description;
    private String occurredAt;
    private Map<String, String> additionalInformation;
}
