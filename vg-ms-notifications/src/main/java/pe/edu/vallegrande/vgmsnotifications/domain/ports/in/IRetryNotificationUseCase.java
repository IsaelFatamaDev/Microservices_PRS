package pe.edu.vallegrande.vgmsnotifications.domain.ports.in;

import pe.edu.vallegrande.vgmsnotifications.domain.models.Notification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IRetryNotificationUseCase {
    Mono<Notification> retryOne(String notificationId);

    Flux<Notification> retryAllFailed();
}
