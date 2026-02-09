package pe.edu.vallegrande.vgmsnotifications.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsnotifications.domain.exceptions.BusinessRuleException;
import pe.edu.vallegrande.vgmsnotifications.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.vgmsnotifications.domain.models.Notification;
import pe.edu.vallegrande.vgmsnotifications.domain.ports.in.IRetryNotificationUseCase;
import pe.edu.vallegrande.vgmsnotifications.domain.ports.out.INotificationRepository;
import pe.edu.vallegrande.vgmsnotifications.domain.services.NotificationDispatcherService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryNotificationUseCaseImpl implements IRetryNotificationUseCase {
    private final INotificationRepository notificationRepository;
    private final NotificationDispatcherService dispatcherService;

    @Override
    public Mono<Notification> retryOne(String notificationId) {
        log.info("Reintentando notificación: {}", notificationId);
        return notificationRepository.findById(notificationId)
            .switchIfEmpty(Mono.error(
                new NotFoundException("Notificación no encontrada: " + notificationId)
            ))
            .flatMap(notification -> {
                if (!notification.canRetry()) {
                    return Mono.error(new BusinessRuleException(
                        "No se puede reintentar. Estado: " + notification.getStatus() + ", Reintentos: " + notification.getRetryCount() + "/3"
                    ));
                }
                notification.prepareForRetry();
                return dispatcherService.dispatch(notification);
            })
            .flatMap(notificationRepository::save)
            .doOnSuccess(n ->log.info("Reintento #{} - {} ({})", n.getRetryCount(), n.getStatus(), n.getId()));
    }

    @Override
    public Flux<Notification> retryAllFailed() {
        log.info("Reintentando todas las notificaciones fallidas...");

        return notificationRepository.findFailedWithRetryAvailable()
            .flatMap(notification -> {
                notification.prepareForRetry();
                return dispatcherService.dispatch(notification)
                    .flatMap(notificationRepository::save);
            })
            .doOnCancel(()-> log.info("Reintento masivo competado"));
    }
}
