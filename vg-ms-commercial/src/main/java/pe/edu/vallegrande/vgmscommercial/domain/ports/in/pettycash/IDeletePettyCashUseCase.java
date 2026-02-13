package pe.edu.vallegrande.vgmscommercial.domain.ports.in.pettycash;

import reactor.core.publisher.Mono;

public interface IDeletePettyCashUseCase {
    Mono<Void> execute(String id, String organizationId);
}