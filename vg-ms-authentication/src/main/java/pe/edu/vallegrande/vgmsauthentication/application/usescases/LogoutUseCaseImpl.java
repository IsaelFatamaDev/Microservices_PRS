package pe.edu.vallegrande.vgmsauthentication.application.usescases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.in.ILogoutUseCase;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.out.IKeycloakClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutUseCaseImpl implements ILogoutUseCase {

    private final IKeycloakClient keycloakClient;

    @Override
    public Mono<Void> execute(String refreshToken, String clientId) {
        log.info("Processing logout request");

        return keycloakClient.revokeToken(refreshToken, clientId)
            .doOnSuccess(v -> log.info("Logout successful - token revoked"))
            .doOnError(error -> log.warn("Logout failed: {}", error.getMessage()))
            .onErrorResume(error ->{
                log.warn("Ignoring logout error: {}", error.getMessage());
                return Mono.empty();
            });
    }

    @Override
    public Mono<Void> executeWithAccessToken(String accessToken) {
        log.info("Processing logout request with access token");

        return keycloakClient.getUserInfo(accessToken)
            .flatMap(userInfo ->{
                log.debug("User logged out: {}", userInfo.get("sub"));
                return Mono.empty();
            }).then();
    }
}
