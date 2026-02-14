package pe.edu.vallegrande.vgmsauthentication.application.events.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsauthentication.application.events.external.*;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.out.IKeycloakClient;
import reactor.core.publisher.Mono;

import java.text.Normalizer;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventHandler {

        private final IKeycloakClient keycloakClient;

        public Mono<Void> handleUserCreated(UserCreatedEvent event) {
                log.info("Processing UserCreatedEvent for user: {}", event.getUserId());

                return generateUniqueUsername(event.getFirstName(), event.getLastName())
                                .flatMap(username -> {
                                        log.info("Generated username for user {}: {}", event.getUserId(), username);
                                        return keycloakClient.createUser(
                                                        event.getUserId(),
                                                        event.getEmail(),
                                                        username,
                                                        event.getFirstName(),
                                                        event.getLastName(),
                                                        event.getDocumentNumber(),
                                                        event.getRole(),
                                                        event.getOrganizationId());
                                })
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

        private Mono<String> generateUniqueUsername(String firstName, String lastName) {
                String[] firstNames = normalize(firstName).split(" ");
                String[] lastNames = normalize(lastName).split(" ");
                String first = firstNames[0];
                String last1 = lastNames[0];
                String last2 = lastNames.length > 1 ? lastNames[1] : null;

                // Attempt 1: primer.apellido1@jass.gob.pe
                String candidate1 = String.format("%s.%s@jass.gob.pe", first, last1);
                return keycloakClient.existsByUsername(candidate1)
                                .flatMap(exists -> {
                                        if (!exists)
                                                return Mono.just(candidate1);

                                        // Attempt 2: primer.apellido1.apellido2@jass.gob.pe
                                        if (last2 != null && !last2.isEmpty()) {
                                                String candidate2 = String.format("%s.%s.%s@jass.gob.pe", first, last1,
                                                                last2);
                                                return keycloakClient.existsByUsername(candidate2)
                                                                .flatMap(exists2 -> {
                                                                        if (!exists2)
                                                                                return Mono.just(candidate2);
                                                                        return findAvailableUsername(first, last1,
                                                                                        last2);
                                                                });
                                        }
                                        return findAvailableUsername(first, last1, null);
                                });
        }

        private Mono<String> findAvailableUsername(String first, String last1, String last2) {
                String base = last2 != null ? String.format("%s.%s.%s", first, last1, last2)
                                : String.format("%s.%s", first, last1);
                return findAvailableUsernameRecursive(base, 2);
        }

        private Mono<String> findAvailableUsernameRecursive(String base, int counter) {
                String candidate = String.format("%s%d@jass.gob.pe", base, counter);
                return keycloakClient.existsByUsername(candidate)
                                .flatMap(exists -> {
                                        if (!exists)
                                                return Mono.just(candidate);
                                        return findAvailableUsernameRecursive(base, counter + 1);
                                });
        }

        private String normalize(String input) {
                if (input == null)
                        return "";
                String normalized = Normalizer.normalize(input.trim(), Normalizer.Form.NFD);
                return normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                                .toLowerCase()
                                .replaceAll("[^a-z\\s]", "")
                                .replaceAll("\\s+", " ")
                                .trim();
        }
}
