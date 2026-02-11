package pe.edu.vallegrande.vgmsnotifications.infrastructure.adapters.out.persistence;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.domain.models.Notification;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationChannel;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationStatus;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.RecordStatus;

@Component
public class NotificationPersistenceMapper {

     public NotificationDocument toDocument(Notification n) {
          return NotificationDocument.builder()
                    .id(n.getId())
                    .userId(n.getUserId())
                    .phoneNumber(n.getPhoneNumber())
                    .recipientName(n.getRecipientName())
                    .type(n.getType().name())
                    .channel(n.getChannel().name())
                    .status(n.getStatus().name())
                    .message(n.getMessage())
                    .imageUrl(n.getImageUrl())
                    .eventSource(n.getEventSource())
                    .eventId(n.getEventId())
                    .retryCount(n.getRetryCount())
                    .failureReason(n.getFailureReason())
                    .recordStatus(n.getRecordStatus().name())
                    .createdAt(n.getCreatedAt())
                    .sentAt(n.getSentAt())
                    .updatedAt(n.getUpdatedAt())
                    .build();
     }

     public Notification toDomain(NotificationDocument doc) {
          return Notification.builder()
                    .id(doc.getId())
                    .userId(doc.getUserId())
                    .phoneNumber(doc.getPhoneNumber())
                    .recipientName(doc.getRecipientName())
                    .type(NotificationType.valueOf(doc.getType()))
                    .channel(NotificationChannel.valueOf(doc.getChannel()))
                    .status(NotificationStatus.valueOf(doc.getStatus()))
                    .message(doc.getMessage())
                    .imageUrl(doc.getImageUrl())
                    .eventSource(doc.getEventSource())
                    .eventId(doc.getEventId())
                    .retryCount(doc.getRetryCount())
                    .failureReason(doc.getFailureReason())
                    .recordStatus(RecordStatus.valueOf(doc.getRecordStatus()))
                    .createdAt(doc.getCreatedAt())
                    .sentAt(doc.getSentAt())
                    .updatedAt(doc.getUpdatedAt())
                    .build();
     }
}
