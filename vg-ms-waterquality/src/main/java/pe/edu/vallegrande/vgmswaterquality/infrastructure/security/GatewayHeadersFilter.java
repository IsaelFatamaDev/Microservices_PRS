package pe.edu.vallegrande.vgmswaterquality.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(1)
@RequiredArgsConstructor
public class GatewayHeadersFilter implements WebFilter {

     private final GatewayHeadersExtractor extractor;

     @Override
     public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
          String userId = extractor.extractUserId(exchange.getRequest());
          String role = extractor.extractUserRole(exchange.getRequest());
          String orgId = extractor.extractOrganizationId(exchange.getRequest());

          AuthenticatedUser user = AuthenticatedUser.builder()
                    .userId(userId != null ? userId : "system")
                    .role(role != null ? role : "USER")
                    .organizationId(orgId)
                    .build();

          return chain.filter(exchange)
                    .contextWrite(ctx -> ctx.put(AuthenticatedUser.class, user));
     }
}
