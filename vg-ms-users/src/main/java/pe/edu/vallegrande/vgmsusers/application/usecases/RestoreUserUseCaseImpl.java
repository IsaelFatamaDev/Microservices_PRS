package pe.edu.vallegrande.vgmsusers.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.vallegrande.vgmsusers.domain.exceptions.BusinessRuleException;
import pe.edu.vallegrande.vgmsusers.domain.exceptions.UserNotFoundException;
import pe.edu.vallegrande.vgmsusers.domain.models.User;
import pe.edu.vallegrande.vgmsusers.domain.ports.in.IRestoreUserUseCase;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.IUserEventPublisher;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.IUserRepository;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestoreUserUseCaseImpl implements IRestoreUserUseCase {

    private final IUserRepository userRepository;
    private final IUserEventPublisher eventPublisher;

    @Override
    @Transactional
    public Mono<User> execute(String id, String restoredBy) {
        log.info("Restoring user: {}", id);
        return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
            .flatMap(user -> {
                if (user.isActive()) {
                    return Mono.error(new BusinessRuleException("El usuario ya esta activo"));
                }

                User restoredUser = user.markAsActive(restoredBy);
                return userRepository.update(restoredUser)
                    .flatMap(saved -> publishRestoreEvent(saved, restoredBy));
            })
            .doOnSuccess(u -> log.info("User restored successfully: {}", u.getId()))
            .doOnError(e -> log.error("Error restoring user: {}", e.getMessage()));
    }

    private Mono<User> publishRestoreEvent(User user, String restoredBy) {
        return eventPublisher.publishUserRestored(
                user.getId(),
                user.getOrganizationId(),
                restoredBy
            )
            .thenReturn(user)
            .onErrorResume(e -> {
                log.warn("Failed to publish restore event: {}", e.getMessage());
                return Mono.just(user);
            });
    }
}