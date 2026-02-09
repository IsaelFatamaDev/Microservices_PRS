package pe.edu.vallegrande.vgmsnotifications.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsnotifications.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.vgmsnotifications.domain.models.Notification;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationStatus;
import pe.edu.vallegrande.vgmsnotifications.domain.ports.in.IGetNotificationHistoryUseCase;
import pe.edu.vallegrande.vgmsnotifications.domain.ports.out.INotificationRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetNotificationHistoryUseCaseImpl implements IGetNotificationHistoryUseCase {


    private final INotificationRepository notificationRepository;

    @Override
    public Flux<Notification> findAll() {
        return notificationRepository.findAll();
    }

    @Override
    public Flux<Notification> findByUserId(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public Flux<Notification> findByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status);
    }

    @Override
    public Mono<Notification> findById(String id) {
        return notificationRepository.findById(id)
            .switchIfEmpty(Mono.error(new NotFoundException("Notificacion no encontrada con ID" + id)));
    }
}
