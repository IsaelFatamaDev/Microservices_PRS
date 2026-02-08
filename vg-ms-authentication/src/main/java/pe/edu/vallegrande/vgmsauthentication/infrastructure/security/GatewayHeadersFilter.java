package pe.edu.vallegrande.vgmsauthentication.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayHeadersFilter implements WebFilter {

    private final GatewayHeadersExtractor headersExtractor;

    public static final String AUTHENTICATED_USER_KEY = "authenticatedUser";

    @Override
    @NonNull
    @SuppressWarnings("null")
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        AuthenticatedUser user = headersExtractor.extract(exchange.getRequest());

        if (user != null) {
            log.debug("Authenticated request from user: {}", user.getUserId());
            return chain.filter(exchange)
                    .contextWrite(ctx -> ctx.put(AUTHENTICATED_USER_KEY, user));
        }

        return chain.filter(exchange);
    }
}
