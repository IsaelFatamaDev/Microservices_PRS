package pe.edu.vallegrande.vgmsusers.domain.ports.out;

import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsusers.domain.models.User;
import reactor.core.publisher.Mono;

import java.util.Map;

@Repository
public interface IUserEventPublisher {

    Mono<Void> publishUserCreated(User user, String createdBy);

    Mono<Void> publishUserUpdated(User user, Map<String, Object> changedFields, String updatedBy);

    Mono<Void> publishUserDeleted(String userId, String organizationId, String deletedBy, String reason);

    Mono<Void> publishUserRestored(String userId, String organizationId, String restoredBy);

    Mono<Void> publishUserPurged(User user, String purgedBy, String reason);

}
