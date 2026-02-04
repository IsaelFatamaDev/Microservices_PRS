package pe.edu.vallegrande.vgmsauthentication.domain.ports.in;

import reactor.core.publisher.Mono;

public interface ILogoutUseCase {

    Mono<Void> execute(String refreshToken, String clientId);

    Mono<Void> executeWithAccessToken(String accessToken);
}
