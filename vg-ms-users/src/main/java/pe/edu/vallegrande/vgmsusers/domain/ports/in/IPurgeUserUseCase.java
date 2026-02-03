package pe.edu.vallegrande.vgmsusers.domain.ports.in;

import reactor.core.publisher.Mono;

public interface IPurgeUserUseCase {

    Mono<Void> execute(String id, String purgedBy, String reason);

}
