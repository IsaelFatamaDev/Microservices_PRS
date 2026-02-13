package pe.edu.vallegrande.vgmscommercial.domain.ports.in.payment;

import pe.edu.vallegrande.vgmscommercial.domain.models.Payment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetPaymentUseCase {
    Mono<Payment> findById(String id, String organizationId);
    Flux<Payment> findAll(String organizationId, String status, String userId, Integer page, Integer size);
    Flux<Payment> findByUserId(String userId, String organizationId);
    Mono<Long> count(String organizationId, String status);
}