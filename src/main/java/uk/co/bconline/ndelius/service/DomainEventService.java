package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.notification.HmppsDomainEventType;

import java.util.HashMap;

public interface DomainEventService
{
    void insertDomainEvent(HmppsDomainEventType eventType, HashMap<String, String> attributes);
}
