package pe.edu.vallegrande.vgmsusers.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.vallegrande.vgmsusers.domain.exceptions.BusinessRuleException;
import pe.edu.vallegrande.vgmsusers.domain.exceptions.UserNotFoundException;
import pe.edu.vallegrande.vgmsusers.domain.models.User;
import pe.edu.vallegrande.vgmsusers.domain.ports.in.IDeleteUserUseCase;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.IUserEventPublisher;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.IUserRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteUserUseCaseImpl implements IDeleteUserUseCase {

    private final IUserRepository userRepository;
    private final IUserEventPublisher eventPublisher;

    @Override
    @Transactional
    public Mono<User> execute(String id, String deletedBy, String reason) {
        log.info("Soft deleting user: {} - Reason: {}", id, reason);

        return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
            .flatMap(user -> {
                if (user.isInactive()) {
                    return Mono.error(new BusinessRuleException(
                        "El usuario ya se encuentra inactivo"
                    ));
                }

                User deletedUser = user.markAsDeleted(deletedBy);

                return userRepository.update(deletedUser)
                    .flatMap(saved -> publishDeleteEvent(saved, reason, deletedBy));
            })
            .doOnSuccess(u -> log.info("User soft deleted successfully: {}", u.getId()))
            .doOnError(e -> log.error("Error soft deleting user: {}", e.getMessage()));
    }

    private Mono<User> publishDeleteEvent(User user, String reason, String deletedBy) {
        return eventPublisher.publishUserDeleted(
                user.getId(),
                user.getOrganizationId(),
                reason,
                deletedBy
            )
            .thenReturn(user)
            .onErrorResume(e -> {
                log.warn("Failed to publish delete event: {}", e.getMessage());
                return Mono.just(user);
            });
    }
}