package pe.edu.vallegrande.vgmscommercial.domain.ports.in.debt;

import reactor.core.publisher.Mono;

public interface IDeleteDebtUseCase {
    Mono<Void> execute(String id, String organizationId);
}