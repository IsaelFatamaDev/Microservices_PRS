package pe.edu.vallegrande.vgmsnotifications.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsnotifications.domain.models.Notification;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationStatus;
import pe.edu.vallegrande.vgmsnotifications.domain.ports.out.INotificationRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements INotificationRepository {

    private final NotificationMongoRepository mongoRepository;
    private final NotificationPersistenceMapper mapper;

    @Override
    public Mono<Notification> save(Notification notification) {
        NotificationDocument doc = mapper.toDocument(notification);
        if (doc == null) {
            return Mono.error(new IllegalArgumentException("Notification document cannot be null"));
        }
        return mongoRepository.save(doc).map(mapper::toDomain);
    }

    @Override
    public Mono<Notification> findById(String id) {
        return mongoRepository.findById(id != null ? id : "").map(mapper::toDomain);
    }

    @Override
    public Flux<Notification> findAll() {
        return mongoRepository.findAll().map(mapper::toDomain);
    }

    @Override
    public Flux<Notification> findByUserId(String userId) {
        return mongoRepository.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public Flux<Notification> findByStatus(NotificationStatus status) {
        return mongoRepository.findByStatus(status.name()).map(mapper::toDomain);
    }

    @Override
    public Flux<Notification> findFailedWithRetryAvailable() {
        return mongoRepository.findByStatusAndRetryCountLessThan(NotificationStatus.FAILED.name(), 3)
                .map(mapper::toDomain);
    }
}
