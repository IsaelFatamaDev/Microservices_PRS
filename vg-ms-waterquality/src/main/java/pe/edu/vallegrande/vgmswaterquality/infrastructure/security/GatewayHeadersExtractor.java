package pe.edu.vallegrande.vgmswaterquality.infrastructure.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class GatewayHeadersExtractor {

     private static final String HEADER_USER_ID = "X-User-Id";
     private static final String HEADER_USER_ROLE = "X-User-Role";
     private static final String HEADER_ORGANIZATION_ID = "X-Organization-Id";

     public String extractUserId(ServerHttpRequest request) {
          return request.getHeaders().getFirst(HEADER_USER_ID);
     }

     public String extractUserRole(ServerHttpRequest request) {
          return request.getHeaders().getFirst(HEADER_USER_ROLE);
     }

     public String extractOrganizationId(ServerHttpRequest request) {
          return request.getHeaders().getFirst(HEADER_ORGANIZATION_ID);
     }
}
