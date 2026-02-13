package pe.edu.vallegrande.vgmsusers.infrastructure.adapters.in.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vgmsusers.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsusers.application.dto.request.CreateUserRequest;
import pe.edu.vallegrande.vgmsusers.application.dto.response.UserResponse;
import pe.edu.vallegrande.vgmsusers.application.mappers.UserMapper;
import pe.edu.vallegrande.vgmsusers.domain.exceptions.BusinessRuleException;
import pe.edu.vallegrande.vgmsusers.domain.exceptions.DuplicateDocumentException;
import pe.edu.vallegrande.vgmsusers.domain.models.User;
import pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.DocumentType;
import pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.Role;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.IUserEventPublisher;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.IUserRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/setup")
@RequiredArgsConstructor
@Slf4j
public class SetupRest {

     private final IUserRepository userRepository;
     private final IUserEventPublisher eventPublisher;
     private final UserMapper userMapper;

     private static final String SUPER_ADMIN_CREATED = "Super Admin created successfully";
     private static final String SYSTEM_SETUP = "SYSTEM_SETUP";

     @PostMapping("/super-admin")
     public Mono<ResponseEntity<ApiResponse<UserResponse>>> createFirstSuperAdmin(
               @Valid @RequestBody CreateUserRequest request) {
          log.info("POST /users/setup/super-admin - Creating first SUPER_ADMIN with document: {}",
                    request.getDocumentNumber());

          if (!"SUPER_ADMIN".equals(request.getRole())) {
               return Mono.just(ResponseEntity
                         .status(HttpStatus.BAD_REQUEST)
                         .body(ApiResponse.error("This endpoint only allows creating SUPER_ADMIN users")));
          }

          if (request.getEmail() == null || request.getEmail().isBlank()) {
               return Mono.just(ResponseEntity
                         .status(HttpStatus.BAD_REQUEST)
                         .body(ApiResponse.error("Email is required for SUPER_ADMIN")));
          }

          return checkNoSuperAdminExists()
                    .then(validateDocumentNotExists(request.getDocumentNumber()))
                    .then(createSuperAdmin(request))
                    .flatMap(this::publishEventAndNotify)
                    .map(user -> ResponseEntity
                              .status(HttpStatus.CREATED)
                              .body(ApiResponse.success(userMapper.toResponse(user), SUPER_ADMIN_CREATED)))
                    .onErrorResume(BusinessRuleException.class, e -> Mono.just(ResponseEntity
                              .status(HttpStatus.CONFLICT)
                              .body(ApiResponse.error(e.getMessage()))))
                    .onErrorResume(DuplicateDocumentException.class, e -> Mono.just(ResponseEntity
                              .status(HttpStatus.CONFLICT)
                              .body(ApiResponse.error(e.getMessage()))));
     }

     @GetMapping("/status")
     public Mono<ResponseEntity<ApiResponse<SetupStatus>>> getSetupStatus() {
          return userRepository.findByRole(Role.SUPER_ADMIN)
                    .hasElements()
                    .map(hasSuperAdmin -> {
                         SetupStatus status = new SetupStatus(!hasSuperAdmin);
                         return ResponseEntity.ok(ApiResponse.success(status, "Setup status retrieved"));
                    });
     }

     private Mono<Void> checkNoSuperAdminExists() {
          return userRepository.findByRole(Role.SUPER_ADMIN)
                    .hasElements()
                    .flatMap(exists -> {
                         if (exists) {
                              return Mono.error(new BusinessRuleException(
                                        "SUPER_ADMIN_EXISTS",
                                        "A SUPER_ADMIN already exists. Use authenticated endpoints to create additional users."));
                         }
                         return Mono.empty();
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

     private Mono<User> createSuperAdmin(CreateUserRequest request) {
          User user = User.builder()
                    .id(UUID.randomUUID().toString())
                    .organizationId("SYSTEM_ORG_ID")
                    .recordStatus(RecordStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .createdBy(SYSTEM_SETUP)
                    .updatedAt(LocalDateTime.now())
                    .updatedBy(SYSTEM_SETUP)
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .documentType(DocumentType.valueOf(request.getDocumentType()))
                    .documentNumber(request.getDocumentNumber())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .address("Sistema Central")
                    .zoneId("SYSTEM_ZONE_ID")
                    .streetId("SYSTEM_STREET_ID")
                    .role(Role.SUPER_ADMIN)
                    .build();

          return userRepository.save(user);
     }

     private Mono<User> publishEventAndNotify(User user) {
          return eventPublisher.publishUserCreated(user, SYSTEM_SETUP)
                    .thenReturn(user)
                    .doOnSuccess(u -> log.info("First SUPER_ADMIN created successfully: {}", u.getId()))
                    .onErrorResume(e -> {
                         log.warn("Event publishing failed for first SUPER_ADMIN, but user was created: {}",
                                   e.getMessage());
                         return Mono.just(user);
                    });
     }

     public record SetupStatus(boolean needsSetup) {
     }
}
