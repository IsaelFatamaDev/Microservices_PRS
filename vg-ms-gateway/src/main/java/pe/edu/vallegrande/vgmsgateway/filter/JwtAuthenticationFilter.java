package pe.edu.vallegrande.vgmsgateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> ctx.getAuthentication())
            .filter(auth -> auth != null && auth.getPrincipal() instanceof Jwt)
            .map(auth -> (Jwt) auth.getPrincipal())
            .map(jwt -> {
                String userId = jwt.hasClaim("userId") ? jwt.getClaimAsString("userId") : jwt.getSubject();
                ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Email", jwt.getClaimAsString("email"))
                    .header("X-User-Roles", String.join(",", jwt.getClaimAsStringList("roles")))
                    .build();
                return exchange.mutate().request(request).build();
            })
            .defaultIfEmpty(exchange)
            .flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}