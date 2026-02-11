package pe.edu.vallegrande.vgmsnotifications.domain.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsnotifications.domain.exceptions.InvalidRecipientException;
import pe.edu.vallegrande.vgmsnotifications.domain.models.Notification;
import pe.edu.vallegrande.vgmsnotifications.domain.ports.out.IWhatsAppClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDispatcherService {

    private final IWhatsAppClient whatsAppClient;

    public Mono<Notification> dispatch(Notification notification) {
        if (!notification.hasValidPhoneNumber()) {
            return Mono.error(new InvalidRecipientException(
                    "Invalid phone number: " + notification.getPhoneNumber()));
        }

        Mono<Boolean> sendResult;
        if (notification.hasImage()) {
            sendResult = whatsAppClient.sendImageMessage(
                    notification.getPhoneNumber(),
                    notification.getImageUrl(),
                    notification.getMessage());
        } else {
            sendResult = whatsAppClient.sendTextMessage(
                    notification.getPhoneNumber(),
                    notification.getMessage());
        }

        return sendResult
                .map(success -> {
                    if (success) {
                        notification.markAsSent();
                    } else {
                        notification.markAsFailed("Evolution API returned false");
                    }
                    return notification;
                })
                .onErrorResume(error -> {
                    notification.markAsFailed(error.getMessage());
                    return Mono.just(notification);
                });
    }
}
