package pe.edu.vallegrande.msdistribution.infrastructure.config;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.UUID;

@Component
public class RequestContextFilter implements WebFilter {

    public static final String CORRELATION_ID = "correlationId";
    public static final String USER_ID = "userId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String correlationId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Correlation-Id");

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        String userId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-User-Id");

        // Agregar a MDC para logs
        MDC.put(CORRELATION_ID, correlationId);
        if (userId != null) {
            MDC.put(USER_ID, userId);
        }

        // Propagar header en la respuesta
        exchange.getResponse()
                .getHeaders()
                .add("X-Correlation-Id", correlationId);

        String finalCorrelationId = correlationId;
        String finalUserId = userId;

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