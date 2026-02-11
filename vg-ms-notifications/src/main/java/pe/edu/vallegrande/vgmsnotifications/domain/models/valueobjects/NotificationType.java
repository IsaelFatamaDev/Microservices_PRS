package pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects;

import lombok.Getter;

@Getter
public enum NotificationType {

    WELCOME("Welcome to the system"),
    PROFILE_UPDATED("Profile updated"),
    ACCOUNT_DEACTIVATED("Account deactivated"),
    ACCOUNT_RESTORED("Account restored"),
    PAYMENT_CONFIRMATION("Payment confirmation"),
    RECEIPT_NOTIFICATION("Receipt notification"),
    OVERDUE_REMINDER("Overdue payment reminder"),
    SERVICE_CUT_WARNING("Service cut warning"),
    SERVICE_CUT_EXECUTED("Service cut executed"),
    SERVICE_RESTORED("Service restored"),
    CUSTOM("Custom notification");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public boolean requiresImage() {
        return this == WELCOME;
    }

    public boolean isUrgent() {
        return this == SERVICE_CUT_WARNING || this == SERVICE_CUT_EXECUTED || this == OVERDUE_REMINDER;
    }
}
