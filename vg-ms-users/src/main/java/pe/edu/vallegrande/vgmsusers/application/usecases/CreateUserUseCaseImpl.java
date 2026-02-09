package pe.edu.vallegrande.vgmsusers.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.vallegrande.vgmsusers.domain.exceptions.BusinessRuleException;
import pe.edu.vallegrande.vgmsusers.domain.exceptions.DuplicateDocumentException;
import pe.edu.vallegrande.vgmsusers.domain.exceptions.ExternalServiceException;
import pe.edu.vallegrande.vgmsusers.domain.models.User;
import pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsusers.domain.ports.in.ICreateUserUseCase;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.*;
import pe.edu.vallegrande.vgmsusers.domain.services.UserAuthorizationService;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateUserUseCaseImpl implements ICreateUserUseCase {

    private final IUserRepository userRepository;
    private final IOrganizationClient organizationClient;
    private final IUserEventPublisher eventPublisher;
    private final INotificationClient notificationClient;
    private final ISecurityContext securityContext;

    @Override
    @Transactional
    public Mono<User> execute(User user, String createdBy) {
        log.info("Creating user with document: {}", user.getDocumentNumber());

        return validateRoleAuthorization(user)
            .then(validateDocumentNotExists(user.getDocumentNumber()))
            .then(validateOrganizationHierarchyIfNeeded(user))
            .then(Mono.fromCallable(() -> {
                user.validateContact();
                return user;
            }))
            .flatMap(validatedUser -> {
                User userToSave = User.builder()
                    .id(UUID.randomUUID().toString())
                    .organizationId(validatedUser.getOrganizationId())
                    .recordStatus(RecordStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .createdBy(createdBy)
                    .updatedAt(LocalDateTime.now())
                    .updatedBy(createdBy)
                    .firstName(validatedUser.getFirstName())
                    .lastName(validatedUser.getLastName())
                    .documentType(validatedUser.getDocumentType())
                    .documentNumber(validatedUser.getDocumentNumber())
                    .email(validatedUser.getEmail())
                    .phone(validatedUser.getPhone())
                    .address(validatedUser.getAddress())
                    .zoneId(validatedUser.getZoneId())
                    .streetId(validatedUser.getStreetId())
                    .role(validatedUser.getRole())
                    .build();

                return userRepository.save(userToSave);
            })
            .flatMap(savedUser -> publishEventAndNotify(savedUser, createdBy))
            .doOnSuccess(u -> log.info("User created: {}", u.getId()))
            .doOnError(e -> log.error("Error creating user: {}", e.getMessage()));
    }

    private Mono<Void> validateRoleAuthorization(User user) {
        return securityContext.getCurrentUserRoles()
            .flatMap(creatorRoles -> {
                try {
                    UserAuthorizationService.validateCanCreateUserWithRole(creatorRoles, user.getRole());
                    return Mono.empty();
                } catch (BusinessRuleException e) {
                    return Mono.error(e);
                }
            });
    }

    private Mono<Void> validateDocumentNotExists(String documentNumber) {
        return userRepository.existsByDocumentNumber(documentNumber)
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new DuplicateDocumentException(documentNumber));
                }
                return Mono.empty();
            });
    }

    private Mono<Void> validateOrganizationHierarchyIfNeeded(User user) {
        // SUPER_ADMIN no requiere validación de organización
        if (user.getRole() == pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.Role.SUPER_ADMIN) {
            log.info("Skipping organization validation for SUPER_ADMIN");
            return Mono.empty();
        }

        return validateOrganizationHierarchy(user);
    }

    private Mono<Void> validateOrganizationHierarchy(User user) {
        return organizationClient.validateHierarchy(
                user.getOrganizationId(),
                user.getZoneId(),
                user.getStreetId()
            )
            .flatMap(isValid -> {
                if (!isValid) {
                    return Mono.error(new BusinessRuleException(
                        "Invalid organization/zone/street combination"
                    ));
                }
                return Mono.empty();
            })
            .onErrorResume(ExternalServiceException.class, e -> {
                log.warn("Organization service unavailable, skipping validation: {}", e.getMessage());
                return Mono.empty();
            }).then();
    }

    private Mono<User> publishEventAndNotify(User user, String createdBy) {
        return eventPublisher.publishUserCreated(user, createdBy)
            .then(sendWelcomeNotification(user))
            .thenReturn(user)
            .onErrorResume(e -> {
                log.warn("Failed to publish event or send notification: {}", e.getMessage());
                return Mono.just(user);
            });
    }

    private Mono<Void> sendWelcomeNotification(User user) {
        if (user.getPhone() == null || user.getPhone().isBlank()) {
            return Mono.empty();
        }

        return notificationClient.sendWelcomeMessage(
                user.getPhone(),
                user.getFirstName(),
                "JASS Digital"
            )
            .onErrorResume(e -> {
                log.warn("Failed to send welcome notification: {}", e.getMessage());
                return Mono.empty();
            });
    }
}
