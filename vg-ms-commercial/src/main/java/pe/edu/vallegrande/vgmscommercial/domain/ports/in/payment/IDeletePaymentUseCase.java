package pe.edu.vallegrande.vgmscommercial.domain.ports.in.payment;

import reactor.core.publisher.Mono;

public interface IDeletePaymentUseCase {
    Mono<Void> execute(String id, String organizationId);
}