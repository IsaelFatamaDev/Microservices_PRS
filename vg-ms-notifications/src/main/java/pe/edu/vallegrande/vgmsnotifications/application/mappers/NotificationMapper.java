package pe.edu.vallegrande.vgmsnotifications.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.application.dto.response.NotificationResponse;
import pe.edu.vallegrande.vgmsnotifications.domain.models.Notification;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
            .id(notification.getId())
            .userId(notification.getUserId())
            .phoneNumber(notification.getPhoneNumber())
            .recipientName(notification.getRecipientName())
            .type(notification.getType().name())
            .channel(notification.getChannel().name())
            .status(notification.getStatus().name())
            .message(notification.getMessage())
            .imageUrl(notification.getImageUrl())
            .eventSource(notification.getEventSource())
            .retryCount(notification.getRetryCount())
            .failureReason(notification.getFailureReason())
            .createdAt(notification.getCreatedAt())
            .sentAt(notification.getSentAt())
            .build();
    }
}
