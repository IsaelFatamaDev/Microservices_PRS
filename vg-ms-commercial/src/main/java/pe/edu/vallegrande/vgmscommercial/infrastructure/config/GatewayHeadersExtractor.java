package pe.edu.vallegrande.vgmscommercial.infrastructure.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayHeadersExtractor {

     public Mono<GatewayHeaders> extract(ServerWebExchange exchange) {
          ServerHttpRequest request = exchange.getRequest();
          String userId = request.getHeaders().getFirst("X-User-Id");
          String organizationId = request.getHeaders().getFirst("X-Organization-Id");
          String roles = request.getHeaders().getFirst("X-User-Roles");

          return Mono.just(GatewayHeaders.builder()
                    .userId(userId != null ? userId : "system")
                    .organizationId(organizationId != null ? organizationId : "default")
                    .roles(roles != null ? roles : "")
                    .build());
     }

     @Data
     @Builder
     @NoArgsConstructor
     @AllArgsConstructor
     public static class GatewayHeaders {
          private String userId;
          private String organizationId;
          private String roles;

          public boolean hasRole(String role) {
               return roles != null && roles.contains(role);
          }

          public boolean isSuperAdmin() {
               return hasRole("SUPER_ADMIN");
          }
     }
}
