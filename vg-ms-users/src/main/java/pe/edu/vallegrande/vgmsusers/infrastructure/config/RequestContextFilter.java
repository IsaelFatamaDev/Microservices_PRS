package pe.edu.vallegrande.vgmsusers.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.UUID;

@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@SuppressWarnings("null")
public class RequestContextFilter implements WebFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String CORRELATION_ID_KEY = "correlationId";
    public static final String USER_ID_KEY = "userId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String correlationId = request.getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        String userId = request.getHeaders().getFirst(USER_ID_HEADER);
        if (userId == null || userId.isBlank()) {
            userId = "anonymous";
        }

        final String finalCorrelationId = correlationId;
        final String finalUserId = userId;

        log.debug("Request: {} {} - correlationId: {}, userId: {}",
                request.getMethod(), request.getPath(), finalCorrelationId, finalUserId);

        exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, finalCorrelationId);
        return chain.filter(exchange)
                .contextWrite(ctx -> {
                    MDC.put(CORRELATION_ID_KEY, finalCorrelationId);
                    MDC.put(USER_ID_KEY, finalUserId);
                    return Context.empty();
                })
                .contextWrite(Context.of(
                        CORRELATION_ID_KEY, finalCorrelationId,
                        USER_ID_KEY, finalUserId))
                .doFinally(signalType -> {
                    MDC.remove(CORRELATION_ID_KEY);
                    MDC.remove(USER_ID_KEY);
                });
    }
}
