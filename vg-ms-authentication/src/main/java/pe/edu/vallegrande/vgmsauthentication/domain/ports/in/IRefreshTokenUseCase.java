package pe.edu.vallegrande.vgmsauthentication.domain.ports.in;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface IRefreshTokenUseCase {

    Mono<Map<String, Object>> execute(String refreshToken, String clientId);
}
