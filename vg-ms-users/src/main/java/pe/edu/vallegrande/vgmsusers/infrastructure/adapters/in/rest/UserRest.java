package pe.edu.vallegrande.vgmsusers.infrastructure.adapters.in.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;
import pe.edu.vallegrande.vgmsusers.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsusers.application.dto.request.CreateUserRequest;
import pe.edu.vallegrande.vgmsusers.application.dto.request.UpdateUserRequest;
import pe.edu.vallegrande.vgmsusers.application.dto.response.UserResponse;
import pe.edu.vallegrande.vgmsusers.application.mappers.UserMapper;
import pe.edu.vallegrande.vgmsusers.domain.ports.in.*;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.ISecurityContext;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserRest {

    private final ICreateUserUseCase createUserUseCase;
    private final IGetUserUseCase getUserUseCase;
    private final IUpdateUserUseCase updateUserUseCase;
    private final IDeleteUserUseCase deleteUserUseCase;
    private final IRestoreUserUseCase restoreUserUseCase;
    private final IPurgeUserUseCase purgeUserUseCase;
    private final UserMapper userMapper;
    private final ISecurityContext securityContext;

    private static final String USER_CREATED = "User created successfully";
    private static final String USER_FOUND = "User retrieved successfully";
    private static final String USERS_FOUND = "Users retrieved successfully";
    private static final String USER_UPDATED = "User updated successfully";
    private static final String USER_DELETED = "User deleted successfully";
    private static final String USER_RESTORED = "User restored successfully";
    private static final String USER_PURGED = "User permanently deleted";
    private static final String PROFILE_FOUND = "Profile retrieved successfully";

    @GetMapping("/me")
    public Mono<ResponseEntity<ApiResponse<UserResponse>>> getMyProfile() {
        log.info("GET /users/me - Getting authenticated user profile");
        
        return securityContext.getCurrentUserId()
            .flatMap(userId -> getUserUseCase.findById(userId)
                .switchIfEmpty(
                    securityContext.getCurrentUserEmail()
                        .flatMap(getUserUseCase::findByEmail)
                )
            )
            .map(user -> ResponseEntity.ok(ApiResponse.success(userMapper.toResponse(user), PROFILE_FOUND)))
            .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("User not found")));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<UserResponse>>> createUser(
        @Valid @RequestBody CreateUserRequest request
    ) {
        log.info("POST /users - Creating user with document: {}", request.getDocumentNumber());

        return validateCreateUserRequest(request)
            .then(securityContext.getCurrentUserId())
            .flatMap(userId -> createUserUseCase.execute(userMapper.toModel(request), userId))
            .map(user -> ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(userMapper.toResponse(user), USER_CREATED))
            );
    }

    private Mono<Void> validateCreateUserRequest(CreateUserRequest request) {
        if ("SUPER_ADMIN".equals(request.getRole())) {
            if (request.getEmail() == null || request.getEmail().isBlank()) {
                return Mono.error(new IllegalArgumentException("Email is required for SUPER_ADMIN"));
            }
        } else {
            if (request.getOrganizationId() == null || request.getOrganizationId().isBlank()) {
                return Mono.error(new IllegalArgumentException("Organization ID is required for non-SUPER_ADMIN roles"));
            }
            if (request.getAddress() == null || request.getAddress().isBlank()) {
                return Mono.error(new IllegalArgumentException("Address is required for non-SUPER_ADMIN roles"));
            }

            if (!"ADMIN".equals(request.getRole())) {
                if (request.getZoneId() == null || request.getZoneId().isBlank()) {
                    return Mono.error(new IllegalArgumentException("Zone ID is required for non-ADMIN roles"));
                }
                if (request.getStreetId() == null || request.getStreetId().isBlank()) {
                    return Mono.error(new IllegalArgumentException("Street ID is required for non-ADMIN roles"));
                }
            }
        }
        return Mono.empty();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<UserResponse>>> getUserById(@Parameter(description = "Id del usuario") @PathVariable String id) {
        log.info("GET /users/{} - Getting user by id", id);
        return getUserUseCase.findById(id)
            .map(user -> ResponseEntity.ok(ApiResponse.success(userMapper.toResponse(user), USER_FOUND)));
    }

    @GetMapping("/document/{documentNumber}")
    public Mono<ResponseEntity<ApiResponse<UserResponse>>> getUserByDocumentNumber(@Parameter(description = "Documento del usuario") @PathVariable String documentNumber) {
        log.info("GET /users/document/{} - Getting user by document", documentNumber);
        return getUserUseCase.findByDocumentNumber(documentNumber)
            .map(user -> ResponseEntity.ok(ApiResponse.success(userMapper.toResponse(user), USER_FOUND)));
    }

    @GetMapping("/email/{email}")
    public Mono<ResponseEntity<ApiResponse<UserResponse>>> getUserByEmail(@Parameter(description = "Email del usuario") @PathVariable String email) {
        log.info("GET /users/email/{} - Getting user by email", email);
        return getUserUseCase.findByEmail(email)
            .map(user -> ResponseEntity.ok(ApiResponse.success(userMapper.toResponse(user), USER_FOUND)));
    }

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<UserResponse>>>> getAllActiveUser() {
        log.info("GET /users - Getting all active users");
        return getUserUseCase.findAllActive()
            .map(userMapper::toResponse)
            .collectList()
            .map(users -> ResponseEntity.ok(ApiResponse.success(users, USERS_FOUND)));
    }

    @GetMapping("/all")
    public Mono<ResponseEntity<ApiResponse<List<UserResponse>>>> getAllUser() {
        log.info("GET /users/all - Getting all users");
        return getUserUseCase.findAll()
            .map(userMapper::toResponse)
            .collectList()
            .map(users -> ResponseEntity.ok(
                ApiResponse.success(users, USERS_FOUND)
            ));
    }

    @GetMapping("/organization")
    public Mono<ResponseEntity<ApiResponse<List<UserResponse>>>> getUsersByMyOrganization(
        @Parameter(description = "Include inactive users")
        @RequestParam(defaultValue = "false") boolean includeInactive
    ) {
        log.info("GET /users/organization - Getting users from my organization, includeInactive: {}", includeInactive);

        return securityContext.getCurrentOrganizationId()
            .flatMap(orgId -> {
                var flux = includeInactive
                    ? getUserUseCase.findByOrganizationId(orgId)
                    : getUserUseCase.findActiveByOrganizationId(orgId);
                return flux.map(userMapper::toResponse).collectList();
            })
            .map(users -> ResponseEntity.ok(ApiResponse.success(users, USERS_FOUND)));
    }

    @GetMapping("/organization/{organizationId}")
    public Mono<ResponseEntity<ApiResponse<List<UserResponse>>>> getUsersByOrganization(
        @Parameter(description = "Organization ID")
        @PathVariable String organizationId,
        @Parameter(description = "Include inactive users")
        @RequestParam(defaultValue = "false") boolean includeInactive
    ) {
        log.info("GET /users/organization/{} - includeInactive: {}", organizationId, includeInactive);

        var flux = includeInactive
            ? getUserUseCase.findByOrganizationId(organizationId)
            : getUserUseCase.findActiveByOrganizationId(organizationId);

        return flux.map(userMapper::toResponse)
            .collectList()
            .map(users -> ResponseEntity.ok(ApiResponse.success(users, USERS_FOUND)));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<UserResponse>>> updateUser(
        @Parameter(description = "User ID") @PathVariable String id,
        @Valid @RequestBody UpdateUserRequest request
    ) {
        log.info("PUT /users/{} - Updating user", id);
        return securityContext.getCurrentUserId()
            .flatMap(userId -> updateUserUseCase.execute(id, userMapper.toModel(request), userId))
            .map(user -> ResponseEntity.ok(ApiResponse.success(userMapper.toResponse(user), USER_UPDATED)));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<UserResponse>>> deleteUser(
        @Parameter(description = "User ID") @PathVariable String id,
        @Parameter(description = "Deletion reason") @RequestParam(required = false) String reason
    ) {
        log.info("DELETE /users/{} - Soft deleting user - Reason: {}", id, reason);
        return securityContext.getCurrentUserId()
            .flatMap(userId -> deleteUserUseCase.execute(id, userId, reason))
            .map(user -> ResponseEntity.ok(ApiResponse.success(userMapper.toResponse(user), USER_DELETED)));
    }

    @PatchMapping("/{id}/restore")
    public Mono<ResponseEntity<ApiResponse<UserResponse>>> restoreUser(
        @Parameter(description = "User ID") @PathVariable String id
    ) {
        log.info("PATCH /users/{}/restore - Restoring user", id);
        return securityContext.getCurrentUserId()
            .flatMap(userId -> restoreUserUseCase.execute(id, userId))
            .map(user -> ResponseEntity.ok(ApiResponse.success(userMapper.toResponse(user), USER_RESTORED)));
    }

    @DeleteMapping("/{id}/purge")
    public Mono<ResponseEntity<ApiResponse<Void>>> purgeUser(
        @Parameter(description = "User ID") @PathVariable String id,
        @Parameter(description = "Deletion reason (required)") @RequestParam String reason
    ) {
        log.info("DELETE /users/{}/purge - Hard deleting user - Reason: {}", id, reason);
        return securityContext.getCurrentUserId()
            .flatMap(userId -> purgeUserUseCase.execute(id, userId, reason))
            .then(Mono.just(ResponseEntity.ok(ApiResponse.<Void>success(null, USER_PURGED))));
    }
}
