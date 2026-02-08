package pe.edu.vallegrande.vgmsauthentication.infrastructure.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GatewayHeadersExtractor {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_EMAIL = "X-User-Email";
    public static final String HEADER_USER_ROLES = "X-User-Roles";
    public static final String HEADER_ORGANIZATION_ID = "X-Organization-Id";

    public AuthenticatedUser extract(ServerHttpRequest request) {
        String userId = getHeader(request, HEADER_USER_ID);
        String email = getHeader(request, HEADER_USER_EMAIL);
        String organizationId = getHeader(request, HEADER_ORGANIZATION_ID);
        Set<String> roles = parseRoles(getHeader(request, HEADER_USER_ROLES));

        if (userId == null) {
            return null;
        }

        return AuthenticatedUser.builder()
                .userId(userId)
                .email(email)
                .organizationId(organizationId)
                .roles(roles)
                .build();
    }

    public boolean isAuthenticated(ServerHttpRequest request) {
        return getHeader(request, HEADER_USER_ID) != null;
    }

    @SuppressWarnings("null")
    private String getHeader(ServerHttpRequest request, String headerName) {
        return request.getHeaders().getFirst(headerName);
    }

    private Set<String> parseRoles(String rolesHeader) {
        if (rolesHeader == null || rolesHeader.isBlank()) {
            return Collections.emptySet();
        }
        return Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
}
