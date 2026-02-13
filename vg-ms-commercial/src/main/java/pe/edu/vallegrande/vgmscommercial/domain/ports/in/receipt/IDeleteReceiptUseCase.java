package pe.edu.vallegrande.vgmscommercial.domain.ports.in.receipt;

import reactor.core.publisher.Mono;

public interface IDeleteReceiptUseCase {
    Mono<Void> execute(String id, String organizationId);
}