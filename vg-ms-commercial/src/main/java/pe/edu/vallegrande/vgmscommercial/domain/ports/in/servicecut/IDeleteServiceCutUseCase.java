package pe.edu.vallegrande.vgmscommercial.domain.ports.in.servicecut;

import reactor.core.publisher.Mono;

public interface IDeleteServiceCutUseCase {
    Mono<Void> execute(String id, String organizationId);
}