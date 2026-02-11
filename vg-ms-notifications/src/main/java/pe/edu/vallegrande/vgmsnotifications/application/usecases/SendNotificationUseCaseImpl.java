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

        private final TemplateEngine templateEngine;
        private final NotificationDispatcherService dispatcherService;
        private final INotificationRepository notificationRepository;

        @Override
        public Mono<Notification> send(String phoneNumber, String recipientName, NotificationType type,
                        Map<String, String> variables, String eventSource, String eventId, String userId) {
                String normalizedPhone = normalizePhoneNumber(phoneNumber);
                log.info("Sending notification [{}] to {} ({})", type, recipientName, normalizedPhone);

                String message = templateEngine.render(type, variables);
                String imageUrl = variables.getOrDefault("logoUrl", null);

                Notification notification = Notification.builder()
                                .phoneNumber(normalizedPhone)
                                .recipientName(recipientName)
                                .type(type)
                                .message(message)
                                .eventId(eventId)
                                .eventSource(eventSource)
                                .userId(userId)
                                .imageUrl(imageUrl)
                                .build();

                return dispatcherService.dispatch(notification)
                                .flatMap(notificationRepository::save)
                                .doOnSuccess(n -> log.info("Notification [{}] - {} ({})", n.getType(), n.getStatus(),
                                                n.getId()))
                                .doOnError(e -> log.error("Error sending notification [{}]: {}", type, e.getMessage()));
        }

        private String normalizePhoneNumber(String phone) {
                if (phone == null || phone.isBlank()) {
                        return phone;
                }
                String cleaned = phone.replaceAll("[^0-9]", "");
                if (cleaned.length() == 9 && cleaned.startsWith("9")) {
                        return "51" + cleaned;
                }
                return cleaned;
        }
}
