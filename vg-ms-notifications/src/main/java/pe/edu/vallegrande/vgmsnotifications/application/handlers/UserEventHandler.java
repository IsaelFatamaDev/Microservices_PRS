package pe.edu.vallegrande.vgmsnotifications.application.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.application.events.UserCreatedEvent;
import pe.edu.vallegrande.vgmsnotifications.application.events.UserDeletedEvent;
import pe.edu.vallegrande.vgmsnotifications.application.events.UserRestoredEvent;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;
import pe.edu.vallegrande.vgmsnotifications.domain.ports.in.ISendNotificationUseCase;
import pe.edu.vallegrande.vgmsnotifications.infrastructure.adapters.out.rest.OrganizationApiClient;
import pe.edu.vallegrande.vgmsnotifications.infrastructure.adapters.out.rest.OrganizationApiClient.OrganizationInfo;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventHandler {

        @Value("${frontend.url}")
        private String frontend;

        private final ISendNotificationUseCase sendNotificationUseCase;
        private final OrganizationApiClient organizationApiClient;

        public Mono<Void> handleUserCreated(UserCreatedEvent event) {
                log.info("Processing user.created for: {}", event.getFullName());

                if (event.getPhone() == null || event.getPhone().isBlank()) {
                        log.warn("Skipping notification - no phone number for user: {}", event.getUserId());
                        return Mono.empty();
                }

                String name = event.getFullName();
                String username = generateUsername(event.getFirstName(), event.getLastName());
                String password = event.getDocumentNumber() != null ? event.getDocumentNumber() : "";

                return organizationApiClient.getOrganizationInfo(event.getOrganizationId())
                                .defaultIfEmpty(OrganizationInfo.empty())
                                .flatMap(orgInfo -> {
                                        Map<String, String> variables = new HashMap<>();
                                        variables.put("name", name);
                                        variables.put("frontendUrl", frontend);
                                        variables.put("organization", orgInfo.name());
                                        variables.put("username", username);
                                        variables.put("password", password);
                                        if (orgInfo.hasLogo()) {
                                                variables.put("logoUrl", orgInfo.logoUrl());
                                        }

                                        return sendNotificationUseCase.send(
                                                        event.getPhone(), name,
                                                        NotificationType.WELCOME, variables,
                                                        "vg-ms-users", event.getEventId(), event.getUserId());
                                })
                                .then();
        }

        private String generateUsername(String firstName, String lastName) {
                String first = firstName != null ? firstName.split(" ")[0].toLowerCase() : "user";
                String last = lastName != null ? lastName.split(" ")[0].toLowerCase() : "jass";
                return String.format("%s.%s@jass.gob.pe", first, last);
        }

        public Mono<Void> handleUserDeleted(UserDeletedEvent event) {
                log.info("Processing user.deleted for: {}", event.getFullName());

                if (event.getPhoneNumber() == null || event.getPhoneNumber().isBlank()) {
                        log.warn("Skipping notification - no phone number for user: {}", event.getUserId());
                        return Mono.empty();
                }

                String name = event.getFullName() != null ? event.getFullName() : "User";
                String reason = event.getReason() != null ? event.getReason() : "Administrative decision";

                Map<String, String> variables = new HashMap<>();
                variables.put("name", name);
                variables.put("reason", reason);

                return sendNotificationUseCase.send(
                                event.getPhoneNumber(), name,
                                NotificationType.ACCOUNT_DEACTIVATED, variables,
                                "vg-ms-users", event.getEventId(), event.getUserId()).then();
        }

        public Mono<Void> handleUserRestored(UserRestoredEvent event) {
                log.info("Processing user.restored for: {}", event.getClientName());

                if (event.getPhoneNumber() == null || event.getPhoneNumber().isBlank()) {
                        log.warn("Skipping notification - no phone number for user: {}", event.getUserId());
                        return Mono.empty();
                }

                String name = event.getClientName() != null ? event.getClientName() : "User";

                Map<String, String> variables = new HashMap<>();
                variables.put("name", name);
                variables.put("frontendUrl", frontend);

                return sendNotificationUseCase.send(
                                event.getPhoneNumber(), name,
                                NotificationType.ACCOUNT_RESTORED, variables,
                                "vg-ms-users", event.getEventId(), event.getUserId()).then();
        }
}
