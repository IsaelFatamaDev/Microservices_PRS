package pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects;

import lombok.Getter;

@Getter
public enum NotificationChannel {

    WHATSAPP("WhatsApp via Evolution API");

    private final String description;

    NotificationChannel(String description) {
        this.description = description;
    }
}
