package pe.edu.vallegrande.vgmsauthentication.application.usescases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsauthentication.domain.exceptions.TokenExpiredException;
import pe.edu.vallegrande.vgmsauthentication.domain.exceptions.TokenInvalidException;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.in.IRefreshTokenUseCase;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.out.IKeycloakClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenUseCaseImpl implements IRefreshTokenUseCase {

    private final IKeycloakClient keycloakClient;

    @Override
    public Mono<Map<String, Object>> execute(String refreshToken, String clientId) {
        log.debug("Refreshing token for client: {}", clientId);
        return keycloakClient.refreshToken(refreshToken, clientId)
            .doOnSuccess(tokens -> log.debug("Token refreshed successfully"))
            .doOnError(error -> log.error("Token refresh failed: {}", error.getMessage()))
            .onErrorMap(this::mapError);
    }

    private Throwable mapError(Throwable error) {
        String message = error.getMessage();

        if (message != null && message.contains("invalid")){
            return TokenInvalidException.revoked();
        }

        if (message != null && message.contains("expired")){
            return TokenExpiredException.refreshToken();
        }

        return new TokenInvalidException("refresh failed");
    }
}
