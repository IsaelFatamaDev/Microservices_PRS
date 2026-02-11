package pe.edu.vallegrande.vgmsnotifications.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationChannel;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationStatus;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    private String id;
    private String userId;
    private String phoneNumber;
    private String recipientName;
    private NotificationType type;

    @Builder.Default
    private NotificationChannel channel = NotificationChannel.WHATSAPP;

    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    private String message;
    private String imageUrl;
    private String eventSource;
    private String eventId;

    @Builder.Default
    private int retryCount = 0;

    private String failureReason;

    @Builder.Default
    private RecordStatus recordStatus = RecordStatus.ACTIVE;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    private LocalDateTime sentAt;

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
                    "Maximum retries reached for notification: " + this.id);
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
