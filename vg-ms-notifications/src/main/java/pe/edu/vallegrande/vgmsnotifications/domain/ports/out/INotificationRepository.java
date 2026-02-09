package pe.edu.vallegrande.vgmsnotifications.domain.ports.out;

import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsnotifications.domain.models.Notification;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface INotificationRepository {
    Mono<Notification> save(Notification notification);

    Mono<Notification> findById(String id);

    Flux<Notification> findAll();

    Flux<Notification> findByUserId(String userId);

    Flux<Notification> findByStatus(NotificationStatus status);

    Flux<Notification> findFailedWithRetryAvailable();
}
