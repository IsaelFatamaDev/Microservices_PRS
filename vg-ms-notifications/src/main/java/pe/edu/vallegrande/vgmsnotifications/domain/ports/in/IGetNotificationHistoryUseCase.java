package pe.edu.vallegrande.vgmsnotifications.domain.ports.in;

import pe.edu.vallegrande.vgmsnotifications.domain.models.Notification;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetNotificationHistoryUseCase {

    Flux<Notification> findAll();
    Flux<Notification> findByUserId(String userId);
    Flux<Notification> findByStatus(NotificationStatus status);
    Mono<Notification> findById(String id);
}
