package pe.edu.vallegrande.vgmsnotifications.domain.models;

import lombok.Getter;
import lombok.Setter;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationChannel;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationStatus;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class Notification {

    private String id;
    private String userId;
    private String phoneNumber;
    private String recipientName;
    private NotificationType type;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String message;
    private String imageUrl;
    private String eventSource;
    private String eventId;
    private int retryCount;
    private String failureReason;
    private RecordStatus recordStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime sentAt;

    public Notification() {
        this.status = NotificationStatus.PENDING;
        this.channel = NotificationChannel.WHATSAPP;
        this.recordStatus = RecordStatus.ACTIVE;
        this.retryCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.failureReason = null;
    }

    public void markAsFailed(String reason) {
        this.status = NotificationStatus.FAILED;
        this.failureReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public void prepareForRetry() {
        if (this.retryCount >= 3) {
            throw new IllegalStateException(
                "Máximo de reintentos alcanzados para la notificación: " + this.id
            );
        }
        this.status = NotificationStatus.RETRYING;
        this.retryCount++;
        this.updatedAt = LocalDateTime.now();
        this.failureReason = null;
    }

    public boolean canRetry() {
        return this.status == NotificationStatus.FAILED && this.retryCount < 3;
    }

    public boolean isSent() {
        return this.status == NotificationStatus.SENT;
    }

    public boolean hasValidPhoneNumber() {
        return this.phoneNumber != null && this.phoneNumber.matches("^\\+?\\d{10,15}$");
    }

    public boolean hasImage() {
        return this.imageUrl != null && !this.imageUrl.isBlank();
    }

    public void deactivate() {
        this.recordStatus = RecordStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.recordStatus == RecordStatus.ACTIVE;
    }

}
