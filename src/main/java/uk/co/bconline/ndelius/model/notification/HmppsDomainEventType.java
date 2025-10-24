package uk.co.bconline.ndelius.model.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HmppsDomainEventType {
    UMT_USERNAME_CHANGED("probation-user.username.changed", "The username for a probation user has been changed"),
    UMT_STAFF_UPDATED("probation.staff.updated", "A staff members details have been updated");

    private final String eventType;
    private final String eventDescription;
}
