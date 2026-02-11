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
        log.info("Retrying notification: {}", notificationId);
        return notificationRepository.findById(notificationId)
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Notification not found: " + notificationId)))
                .flatMap(notification -> {
                    if (!notification.canRetry()) {
                        return Mono.error(new BusinessRuleException(
                                "Cannot retry. Status: " + notification.getStatus() + ", Retries: "
                                        + notification.getRetryCount() + "/3"));
                    }
                    notification.prepareForRetry();
                    return dispatcherService.dispatch(notification);
                })
                .flatMap(notificationRepository::save)
                .doOnSuccess(n -> log.info("Retry #{} - {} ({})", n.getRetryCount(), n.getStatus(), n.getId()));
    }

    @Override
    public Flux<Notification> retryAllFailed() {
        log.info("Retrying all failed notifications...");

        return notificationRepository.findFailedWithRetryAvailable()
                .flatMap(notification -> {
                    notification.prepareForRetry();
                    return dispatcherService.dispatch(notification)
                            .flatMap(notificationRepository::save);
                })
                .doOnComplete(() -> log.info("Massive retry completed"));
    }
}
