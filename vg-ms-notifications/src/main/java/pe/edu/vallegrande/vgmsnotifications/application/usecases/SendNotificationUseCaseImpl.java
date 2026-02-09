package pe.edu.vallegrande.vgmsnotifications.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsnotifications.application.templates.TemplateEngine;
import pe.edu.vallegrande.vgmsnotifications.domain.models.Notification;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;
import pe.edu.vallegrande.vgmsnotifications.domain.ports.in.ISendNotificationUseCase;
import pe.edu.vallegrande.vgmsnotifications.domain.ports.out.INotificationRepository;
import pe.edu.vallegrande.vgmsnotifications.domain.services.NotificationDispatcherService;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendNotificationUseCaseImpl implements ISendNotificationUseCase {

    private static final String WELCOME_IMAGE_URL = "https://drive.google.com/uc?export=view&id=1FZS8ClszNml6b0yl3vRcxj2ZOc6vkYgs";

    private final TemplateEngine templateEngine;
    private final NotificationDispatcherService dispatcherService;
    private final INotificationRepository notificationRepository;

    @Override
    public Mono<Notification> send(String phoneNumber, String recipientName, NotificationType type, Map<String, String> variables, String eventSource, String eventId, String userId) {
        log.info("Enviando notificación [{}] a {} ({})", type, recipientName, phoneNumber);

        String message = templateEngine.render(type, variables);
        Notification notification = new Notification();
        notification.setPhoneNumber(phoneNumber);
        notification.setRecipientName(recipientName);
        notification.setType(type);
        notification.setMessage(message);
        notification.setEventId(eventId);
        notification.setEventSource(eventSource);
        notification.setUserId(userId);

        if (type.requiresImage()) {
            notification.setImageUrl(WELCOME_IMAGE_URL);
        }

        return dispatcherService.dispatch(notification)
            .flatMap(notificationRepository::save)
            .doOnSuccess(n -> log.info("Notificatión [{}] - {} ({})", n.getType(), n.getStatus(), n.getId()))
            .doOnError(e -> log.error("Error enviando notificación [{}]: {}",
                type, e.getMessage()));
    }
}
