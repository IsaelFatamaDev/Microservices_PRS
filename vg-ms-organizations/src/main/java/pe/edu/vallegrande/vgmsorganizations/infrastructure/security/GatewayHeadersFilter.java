package pe.edu.vallegrande.vgmsorganizations.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayHeadersFilter implements WebFilter {

    public static final String AUTHENTICATED_USER_KEY = "authenticatedUser";

    private final GatewayHeadersExtractor extractor;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        AuthenticatedUser user = extractor.extract(exchange.getRequest().getHeaders());

        return chain.filter(exchange)
            .contextWrite(Context.of(AUTHENTICATED_USER_KEY, user));
    }
}