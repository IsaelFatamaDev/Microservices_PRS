package pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects;

import lombok.Getter;

@Getter
public enum NotificationStatus {

    PENDING("Pending delivery"),
    SENT("Successfully sent"),
    FAILED("Delivery failed"),
    RETRYING("Retrying delivery");

    private final String description;

    NotificationStatus(String description) {
        this.description = description;
    }

    public boolean hasIssue() {
        return this == FAILED || this == RETRYING;
    }
}
