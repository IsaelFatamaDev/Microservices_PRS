package pe.edu.vallegrande.vgmsorganizations.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class GatewayHeadersExtractor {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_ORGANIZATION_ID = "X-Organization-Id";
    private static final String HEADER_EMAIL = "X-User-Email";
    private static final String HEADER_ROLES = "X-Roles";

    public AuthenticatedUser extract(HttpHeaders headers) {
        String userId = headers.getFirst(HEADER_USER_ID);
        String organizationId = headers.getFirst(HEADER_ORGANIZATION_ID);
        String email = headers.getFirst(HEADER_EMAIL);
        String rolesHeader = headers.getFirst(HEADER_ROLES);

        List<String> roles = Collections.emptyList();
        if (rolesHeader != null && !rolesHeader.isBlank()) {
            roles = Arrays.asList(rolesHeader.split(","));
        }

        log.debug("Headers extracted - userId: {}, orgId: {}, roles: {}", userId, organizationId, roles);

        return AuthenticatedUser.builder()
            .userId(userId)
            .organizationId(organizationId)
            .email(email)
            .roles(roles)
            .build();
    }
}
