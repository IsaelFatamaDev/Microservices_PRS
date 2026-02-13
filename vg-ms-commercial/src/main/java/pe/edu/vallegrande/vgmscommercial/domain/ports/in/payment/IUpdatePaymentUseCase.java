package pe.edu.vallegrande.vgmscommercial.domain.ports.in.payment;

import pe.edu.vallegrande.vgmscommercial.domain.models.Payment;
import reactor.core.publisher.Mono;

public interface IUpdatePaymentUseCase {
    Mono<Payment> execute(String id, Payment payment);
}