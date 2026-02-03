package pe.edu.vallegrande.vgmsusers.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.Role;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GatewayHeadersExtractor {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_ROLES = "X-User-Roles";
    public static final String HEADER_ORGANIZATION_ID = "X-Organization-Id";
    public static final String HEADER_USER_EMAIL = "X-User-Email";

    public AuthenticatedUser extractFromRequest(ServerHttpRequest request) {
        String userId = request.getHeaders().getFirst(HEADER_USER_ID);
        String rolesHeader = request.getHeaders().getFirst(HEADER_USER_ROLES);
        String organizationId = request.getHeaders().getFirst(HEADER_ORGANIZATION_ID);
        String email = request.getHeaders().getFirst(HEADER_USER_EMAIL);

        if (userId == null || userId.isBlank()) {
            log.debug("No user ID in headers, returning anonymous user");
            return AuthenticatedUser.anonymous();
        }

        Set<Role> roles = parseRoles(rolesHeader);

        log.debug("Extracted from Gateway headers - userId: {}, orgId: {}, roles: {}",
            userId, organizationId, roles);

        return AuthenticatedUser.builder()
            .userId(userId)
            .organizationId(organizationId)
            .email(email)
            .roles(roles)
            .authenticated(true)
            .build();
    }

    private Set<Role> parseRoles(String rolesHeader) {
        if (rolesHeader == null || rolesHeader.isBlank()) {
            return Collections.emptySet();
        }

        return Arrays.stream(rolesHeader.split(","))
            .map(String::trim)
            .filter(this::isValidRole)
            .map(Role::valueOf)
            .collect(Collectors.toSet());
    }

    private boolean isValidRole(String roleName) {
        try {
            Role.valueOf(roleName);
            return true;
        } catch (IllegalArgumentException e) {
            log.debug("Ignoring unknown role from header: {}", roleName);
            return false;
        }
    }
}
