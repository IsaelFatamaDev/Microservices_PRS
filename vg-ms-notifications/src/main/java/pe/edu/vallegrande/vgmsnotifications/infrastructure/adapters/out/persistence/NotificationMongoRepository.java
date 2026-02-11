package pe.edu.vallegrande.vgmsnotifications.infrastructure.adapters.out.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface NotificationMongoRepository extends ReactiveMongoRepository<NotificationDocument, String> {

    Flux<NotificationDocument> findByUserId(String userId);

    Flux<NotificationDocument> findByStatus(String status);

    Flux<NotificationDocument> findByStatusAndRetryCountLessThan(String status, int maxRetries);
}