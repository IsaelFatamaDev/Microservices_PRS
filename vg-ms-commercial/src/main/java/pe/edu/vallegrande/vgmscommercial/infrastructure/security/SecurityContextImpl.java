package pe.edu.vallegrande.vgmscommercial.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ISecurityContext;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SecurityContextImpl implements ISecurityContext {

     @Override
     public Mono<String> getCurrentUserId() {
          return Mono.deferContextual(ctx -> {
               if (ctx.hasKey(ServerWebExchange.class)) {
                    ServerWebExchange exchange = ctx.get(ServerWebExchange.class);
                    ServerHttpRequest request = exchange.getRequest();
                    String userId = request.getHeaders().getFirst("X-User-Id");
                    if (userId != null && !userId.isEmpty()) {
                         return Mono.just(userId);
                    }
               }
               return Mono.just("system");
          });
     }

     @Override
     public Mono<String> getCurrentOrganizationId() {
          return Mono.deferContextual(ctx -> {
               if (ctx.hasKey(ServerWebExchange.class)) {
                    ServerWebExchange exchange = ctx.get(ServerWebExchange.class);
                    ServerHttpRequest request = exchange.getRequest();
                    String orgId = request.getHeaders().getFirst("X-Organization-Id");
                    if (orgId != null && !orgId.isEmpty()) {
                         return Mono.just(orgId);
                    }
               }
               return Mono.just("default");
          });
     }

     @Override
     public Mono<Boolean> hasRole(String role) {
          return Mono.deferContextual(ctx -> {
               if (ctx.hasKey(ServerWebExchange.class)) {
                    ServerWebExchange exchange = ctx.get(ServerWebExchange.class);
                    ServerHttpRequest request = exchange.getRequest();
                    String roles = request.getHeaders().getFirst("X-User-Roles");
                    if (roles != null) {
                         return Mono.just(roles.contains(role));
                    }
               }
               return Mono.just(false);
          });
     }

     @Override
     public Mono<Boolean> isSuperAdmin() {
          return hasRole("SUPER_ADMIN");
     }
}
