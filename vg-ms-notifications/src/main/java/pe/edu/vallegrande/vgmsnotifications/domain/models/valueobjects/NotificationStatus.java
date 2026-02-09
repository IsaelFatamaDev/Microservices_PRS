package pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects;

import lombok.Getter;

@Getter
public enum NotificationStatus {
    PENDING("Pendiente de envío"),
    SENT("Enviado exitósamente"),
    FAILED("Fallo en el envío"),
    RETRYING("Reintando envío");

    private final String description;

    NotificationStatus(String description){
        this.description = description;
    }

    public boolean hasIssue(){
        return this == FAILED || this == RETRYING;
    }
}
