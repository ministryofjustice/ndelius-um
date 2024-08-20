package uk.co.bconline.ndelius.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.co.bconline.ndelius.model.notification.HmppsDomainEventType;

import java.util.Map;

public interface DomainEventService
{
    void insertDomainEvent(HmppsDomainEventType eventType, Map<String, String> attributes) throws JsonProcessingException;
}
