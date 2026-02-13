package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class GatewayHeadersExtractor {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USERNAME = "X-Username";
    private static final String HEADER_ROLE = "X-User-Role";

    public AuthenticatedUser extract(ServerHttpRequest request) {
        return AuthenticatedUser.builder()
                .userId(request.getHeaders().getFirst(HEADER_USER_ID))
                .username(request.getHeaders().getFirst(HEADER_USERNAME))
                .role(request.getHeaders().getFirst(HEADER_ROLE))
                .build();
    }

    public String extractUsername(ServerHttpRequest request) {
        String username = request.getHeaders().getFirst(HEADER_USERNAME);
        return username != null ? username : "system";
    }
}
