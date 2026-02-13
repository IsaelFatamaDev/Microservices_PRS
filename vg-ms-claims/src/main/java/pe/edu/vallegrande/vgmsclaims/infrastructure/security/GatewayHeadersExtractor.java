package pe.edu.vallegrande.vgmsclaims.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Extracts authenticated user information from gateway headers.
 */
@Slf4j
@Component
public class GatewayHeadersExtractor {

    // Headers personalizados of the gateway
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_EMAIL = "X-User-Email";
    public static final String HEADER_ORGANIZATION_ID = "X-Organization-Id";
    public static final String HEADER_ROLES = "X-User-Roles";

    /**
     * Extracts authenticated user from request headers.
     */
    public Optional<AuthenticatedUser> extractUser(ServerHttpRequest request) {
        String userId = getHeader(request, HEADER_USER_ID);

        if (userId == null || userId.isEmpty()) {
            log.debug("Authenticated user header not found");
            return Optional.empty();
        }

        AuthenticatedUser user = AuthenticatedUser.builder()
                .userId(userId)
                .email(getHeader(request, HEADER_EMAIL))
                .organizationId(getHeader(request, HEADER_ORGANIZATION_ID))
                .roles(parseCommaSeparated(getHeader(request, HEADER_ROLES)))
                .build();

        log.debug("User extracted from headers: {} (org: {})", user.getUserId(), user.getOrganizationId());
        return Optional.of(user);
    }

    /**
     * Extracts user ID from request headers.
     */
    public Optional<String> extractUserId(ServerHttpRequest request) {
        return Optional.ofNullable(getHeader(request, HEADER_USER_ID))
                .filter(s -> !s.isEmpty());
    }

    /**
     * Extracts organization ID from request headers.
     */
    public Optional<String> extractOrganizationId(ServerHttpRequest request) {
        return Optional.ofNullable(getHeader(request, HEADER_ORGANIZATION_ID))
                .filter(s -> !s.isEmpty());
    }

    /**
     * Extracts roles from request headers.
     */
    public List<String> extractRoles(ServerHttpRequest request) {
        return parseCommaSeparated(getHeader(request, HEADER_ROLES));
    }

    private String getHeader(ServerHttpRequest request, String headerName) {
        return request.getHeaders().getFirst(headerName);
    }

    private List<String> parseCommaSeparated(String value) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
