package pe.edu.vallegrande.vgmsusers.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.vallegrande.vgmsusers.domain.exceptions.BusinessRuleException;
import pe.edu.vallegrande.vgmsusers.domain.exceptions.UserNotFoundException;
import pe.edu.vallegrande.vgmsusers.domain.models.User;
import pe.edu.vallegrande.vgmsusers.domain.ports.in.IUpdateUserUseCase;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.IUserEventPublisher;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.IUserRepository;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateUserUseCaseImpl implements IUpdateUserUseCase {

    private final IUserRepository userRepository;
    private final IUserEventPublisher eventPublisher;

    @Override
    @Transactional
    public Mono<User> execute(String id, User userData, String updatedBy) {
        log.info("Updating user: {}", id);

        return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
            .flatMap(existingUser -> {
                if (existingUser.isInactive()) {
                    return Mono.error(new BusinessRuleException(
                        "Cannot update an inactive user"
                    ));
                }

                Map<String, Object> changedFields = detectChanges(existingUser, userData);

                User updatedUser = existingUser.updateWith(
                    userData.getFirstName(),
                    userData.getLastName(),
                    userData.getEmail(),
                    userData.getPhone(),
                    userData.getAddress(),
                    updatedBy
                );

                return userRepository.update(updatedUser)
                    .flatMap(saved -> publishUpdateEvent(saved, changedFields, updatedBy));
            })
            .doOnSuccess(u -> log.info("User updated: {}", u.getId()))
            .doOnError(e -> log.error("Error updating user: {}", e.getMessage()));
    }

    private Map<String, Object> detectChanges(User existingUser, User newData) {
        Map<String, Object> changes = new HashMap<>();

        if (newData.getFirstName() != null && !newData.getFirstName().equals(existingUser.getFirstName())) {
            changes.put("firstName", newData.getFirstName());
        }

        if (newData.getLastName() != null && !newData.getLastName().equals(existingUser.getLastName())) {
            changes.put("lastName", newData.getLastName());
        }

        if (newData.getEmail() != null && !newData.getEmail().equals(existingUser.getEmail())) {
            changes.put("email", newData.getEmail());
        }

        if (newData.getPhone() != null && !newData.getPhone().equals(existingUser.getPhone())) {
            changes.put("phone", newData.getPhone());
        }

        if (newData.getAddress() != null && !newData.getAddress().equals(existingUser.getAddress())) {
            changes.put("address", newData.getAddress());
        }

        if (newData.getAddress() != null && !newData.getAddress().equals(existingUser.getAddress())) {
            changes.put("address", newData.getAddress());
        }

        return changes;
    }

    private Mono<User> publishUpdateEvent(User user, Map<String, Object> changedFields, String updatedBy) {
        if (changedFields.isEmpty()) {
            return Mono.just(user);
        }

        return eventPublisher.publishUserUpdated(user, changedFields, updatedBy)
            .thenReturn(user)
            .onErrorResume(e -> {
                log.warn("Failed to publish event: {}", e.getMessage());
                return Mono.just(user);
            });
    }


}
