package pe.edu.vallegrande.vgmsauthentication.domain.ports.in;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface IValidateTokenUseCase {

    Mono<Map<String, Object>> execute(String accessToken);

    Mono<Map<String, Object>> introspect(String token, String clientId, String clientSecret);

    Mono<Map<String, Object>> getUserInfo(String accessToken);
}
