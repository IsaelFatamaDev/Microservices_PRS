package pe.edu.vallegrande.vgmswaterquality.infrastructure.config;

import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.UUID;

@Component
@SuppressWarnings("null")
public class RequestContextFilter implements WebFilter {

    private static final String CORRELATION_ID = "correlationId";
    private static final String USER_ID = "userId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-Id");
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");

        MDC.put(CORRELATION_ID, correlationId);
        if (userId != null) {
            MDC.put(USER_ID, userId);
        }

        exchange.getResponse().getHeaders().add("X-Correlation-Id", correlationId);

        final String finalCorrelationId = correlationId;
        final String finalUserId = userId;

        return chain.filter(exchange)
                .contextWrite(ctx -> {
                    Context context = ctx.put(CORRELATION_ID, finalCorrelationId);
                    if (finalUserId != null) {
                        context = context.put(USER_ID, finalUserId);
                    }
                    return context;
                })
                .doFinally(signalType -> MDC.clear());
    }
}