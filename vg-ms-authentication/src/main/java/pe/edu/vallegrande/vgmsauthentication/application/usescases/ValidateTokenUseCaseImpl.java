package pe.edu.vallegrande.vgmsauthentication.application.usescases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsauthentication.domain.exceptions.TokenExpiredException;
import pe.edu.vallegrande.vgmsauthentication.domain.exceptions.TokenInvalidException;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.in.IValidateTokenUseCase;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.out.IKeycloakClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ValidateTokenUseCaseImpl implements IValidateTokenUseCase {

    private final IKeycloakClient keycloakClient;

    @Override
    public Mono<Map<String, Object>> execute(String accessToken) {
        log.debug("Validating access token");
        return keycloakClient.getUserInfo(accessToken)
            .doOnSuccess(info -> log.debug("Token valid for user: {}", info.get("sub")))
            .doOnError(error -> log.error("Token validation failed: {}", error.getMessage()))
            .onErrorMap(this::mapError);
    }

    @Override
    public Mono<Map<String, Object>> introspect(String token, String clientId, String clientSecret) {
        log.debug("Introspecting token");

        return keycloakClient.introspectToken(token, clientId, clientSecret)
            .flatMap(result -> {
                Boolean active = (Boolean) result.get("active");
                if(Boolean.FALSE.equals(active)){
                    return Mono.error(TokenInvalidException.revoked());
                }
                return Mono.just(result);
            });
    }

    @Override
    public Mono<Map<String, Object>> getUserInfo(String accessToken) {
        return keycloakClient.getUserInfo(accessToken)
            .onErrorMap(this::mapError);
    }

    private Throwable mapError(Throwable error) {
        String message = error.getMessage();

        if(message != null && message.contains("401")){
            return TokenExpiredException.accessToken();
        }

        if(message != null && message.contains("invalid")){
            return new TokenInvalidException();
        }

        return new TokenInvalidException("validation failed");
    }

}
