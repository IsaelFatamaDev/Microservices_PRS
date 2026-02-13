package pe.edu.vallegrande.vgmsclaims.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * Filter that extracts user information from gateway headers
 * and places it in the reactive context for later use.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayHeadersFilter implements WebFilter {

    public static final String USER_CONTEXT_KEY = "authenticatedUser";

    private final GatewayHeadersExtractor headersExtractor;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return headersExtractor.extractUser(exchange.getRequest())
                .map(user -> chain.filter(exchange)
                        .contextWrite(Context.of(USER_CONTEXT_KEY, user)))
                .orElseGet(() -> chain.filter(exchange));
    }

    /**
     * Gets the authenticated user from the reactive context.
     */
    public static Mono<AuthenticatedUser> getCurrentUser() {
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey(USER_CONTEXT_KEY)) {
                return Mono.just(ctx.get(USER_CONTEXT_KEY));
            }
            return Mono.empty();
        });
    }

    /**
     * Gets the current user ID from the reactive context.
     */
    public static Mono<String> getCurrentUserId() {
        return getCurrentUser().map(AuthenticatedUser::getUserId);
    }

    /**
     * Gets the current organization ID from the reactive context.
     */
    public static Mono<String> getCurrentOrganizationId() {
        return getCurrentUser().map(AuthenticatedUser::getOrganizationId);
    }
}
