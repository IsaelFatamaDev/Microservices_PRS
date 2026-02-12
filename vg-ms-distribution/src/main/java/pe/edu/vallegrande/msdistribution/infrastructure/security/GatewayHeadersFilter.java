package pe.edu.vallegrande.msdistribution.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * Filtro reactivo que intercepta todas las peticiones HTTP y extrae
 * la información del usuario autenticado desde los headers del API Gateway.
 * Almacena el usuario en el contexto reactivo para acceso posterior.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayHeadersFilter implements WebFilter {
    
    /**
     * Clave para almacenar el usuario autenticado en el contexto reactivo
     */
    public static final String AUTHENTICATED_USER_KEY = "authenticatedUser";
    
    private final GatewayHeadersExtractor headersExtractor;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Extraer usuario de los headers
        AuthenticatedUser user = headersExtractor.extract(exchange.getRequest().getHeaders());
        
        log.debug("Processing request with user: {}", user.getUserId());
        
        // Almacenar en el contexto reactivo y continuar la cadena
        return chain.filter(exchange)
                .contextWrite(Context.of(AUTHENTICATED_USER_KEY, user));
    }
}
