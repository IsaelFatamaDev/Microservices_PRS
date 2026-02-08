package pe.edu.vallegrande.vgmsauthentication.application.events.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsauthentication.application.events.external.*;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.out.IKeycloakClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventHandler {

        private final IKeycloakClient keycloakClient;

        public Mono<Void> handleUserCreated(UserCreatedEvent event) {
                log.info("Processing UserCreatedEvent for user: {}", event.getUserId());

                return keycloakClient.createUser(
                                event.getUserId(),
                                event.getEmail(),
                                generateUsername(event.getFirstName(), event.getLastName()),
                                event.getFirstName(),
                                event.getLastName(),
                                event.getDocumentNumber(),
                                event.getRole(),
                                event.getOrganizationId())
                                .doOnSuccess(keycloakId -> log.info(
                                                "User created in Keycloak. UserId: {}, KeycloakId: {}",
                                                event.getUserId(), keycloakId))
                                .doOnError(error -> log.error(
                                                "Failed to create user in Keycloak: {}. Error: {}",
                                                event.getUserId(), error.getMessage()))
                                .then();
        }

        public Mono<Void> handleUserUpdated(UserUpdatedEvent event) {
                log.info("Processing UserUpdatedEvent for user: {}", event.getUserId());

                return keycloakClient.updateUser(
                                event.getUserId(),
                                event.getEmail(),
                                event.getFirstName(),
                                event.getLastName())
                                .then(updateRoleIfChanged(event))
                                .doOnSuccess(v -> log.info("User updated in Keycloak: {}", event.getUserId()))
                                .doOnError(error -> log.error(
                                                "Failed to update user in Keycloak: {}. Error: {}",
                                                event.getUserId(), error.getMessage()));
        }

        public Mono<Void> handleUserDeleted(UserDeletedEvent event) {
                log.info("Processing UserDeletedEvent for user: {}", event.getUserId());

                return keycloakClient.disableUser(event.getUserId())
                                .doOnSuccess(v -> log.info("User disabled in Keycloak: {}", event.getUserId()))
                                .doOnError(error -> log.error(
                                                "Failed to disable user in Keycloak: {}. Error: {}",
                                                event.getUserId(), error.getMessage()));
        }

        public Mono<Void> handleUserRestored(UserRestoredEvent event) {
                log.info("Processing UserRestoredEvent for user: {}", event.getUserId());

                return keycloakClient.enableUser(event.getUserId())
                                .doOnSuccess(v -> log.info("User enabled in Keycloak: {}", event.getUserId()))
                                .doOnError(error -> log.error(
                                                "Failed to enable user in Keycloak: {}. Error: {}",
                                                event.getUserId(), error.getMessage()));
        }

        public Mono<Void> handleUserPurged(UserPurgedEvent event) {
                log.info("Processing UserPurgedEvent for user: {}", event.getUserId());

                return keycloakClient.deleteUser(event.getUserId())
                                .doOnSuccess(v -> log.info("User deleted from Keycloak: {}", event.getUserId()))
                                .doOnError(error -> log.error(
                                                "Failed to delete user from Keycloak: {}. Error: {}",
                                                event.getUserId(), error.getMessage()));
        }

        private Mono<Void> updateRoleIfChanged(UserUpdatedEvent event) {
                if (event.getRole() == null) {
                        return Mono.empty();
                }
                return keycloakClient.assignRole(event.getUserId(), event.getRole());
        }

        private String generateUsername(String firstName, String lastName) {
                String first = firstName.split(" ")[0].toLowerCase();
                String last = lastName.split(" ")[0].toLowerCase();
                return String.format("%s.%s@jass.gob.pe", first, last);
        }
}
