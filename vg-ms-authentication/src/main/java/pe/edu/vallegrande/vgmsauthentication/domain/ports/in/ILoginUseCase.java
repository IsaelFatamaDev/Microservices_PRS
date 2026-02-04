package pe.edu.vallegrande.vgmsauthentication.domain.ports.in;

import pe.edu.vallegrande.vgmsauthentication.domain.models.UserCredentials;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

public interface ILoginUseCase {
    Mono<Map<String, Object>> execute(UserCredentials credentials);
}
