package uk.co.bconline.ndelius.model.notification;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsMessageRequest implements Serializable
{
    private HmppsDomainEventType eventType;
    private Map<String, String> additionalInformation;
}
