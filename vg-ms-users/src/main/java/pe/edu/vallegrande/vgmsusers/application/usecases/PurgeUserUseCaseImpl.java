package pe.edu.vallegrande.vgmsusers.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.vallegrande.vgmsusers.domain.exceptions.BusinessRuleException;
import pe.edu.vallegrande.vgmsusers.domain.exceptions.UserNotFoundException;
import pe.edu.vallegrande.vgmsusers.domain.models.User;
import pe.edu.vallegrande.vgmsusers.domain.ports.in.IPurgeUserUseCase;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.IUserEventPublisher;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.IUserRepository;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class PurgeUserUseCaseImpl implements IPurgeUserUseCase {

    private final IUserRepository userRepository;
    private final IUserEventPublisher eventPublisher;

    @Override
    @Transactional
    public Mono<Void> execute(String id, String purgedBy, String reason) {
        log.warn("Hard deleting user: {} - Reason: {}", id, reason);
        return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
            .flatMap(user -> {
                if (user.isActive()) {
                    return Mono.error(new BusinessRuleException(
                        "Only inactive users can be hard deleted"
                    ));
                }

                User snapshot = user;

                return userRepository.deleteById(id)
                    .then(publishPurgeEvent(snapshot, reason, purgedBy));
            })
            .doOnSuccess(v -> log.warn("User hard deleted successfully: {}", id))
            .doOnError(e -> log.error("Error hard deleting user: {}", e.getMessage()));
    }

    private Mono<Void> publishPurgeEvent(User user, String reason, String purgedBy) {
        return eventPublisher.publishUserPurged(user, reason, purgedBy)
            .onErrorResume(e -> {
                log.error("Failed to publish purge event for user {}: {}", user.getId(), e.getMessage());
                return Mono.empty();
            });
    }
}
