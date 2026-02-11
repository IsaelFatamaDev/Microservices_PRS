package pe.edu.vallegrande.vgmsusers.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class GatewayHeadersFilter implements WebFilter {

    public static final String AUTHENTICATED_USER_KEY = "AUTHENTICATED_USER";

    private final GatewayHeadersExtractor headersExtractor;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        AuthenticatedUser user = headersExtractor.extractFromRequest(exchange.getRequest());

        log.debug("GatewayHeadersFilter - User: {}, Authenticated: {}",
                user.getUserId(), user.isAuthenticated());

        return chain.filter(exchange)
                .contextWrite(Context.of(AUTHENTICATED_USER_KEY, user));
    }
}
