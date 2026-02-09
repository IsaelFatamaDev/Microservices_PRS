package pe.edu.vallegrande.vgmsnotifications.domain.ports.in;

import pe.edu.vallegrande.vgmsnotifications.domain.models.Notification;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ISendNotificationUseCase {
    Mono<Notification> send(
        String phoneNumber,
        String recipientName,
        NotificationType type,
        Map<String, String> variables,
        String eventSource,
        String eventId,
        String userId
    );
}
