package pe.edu.vallegrande.vgmscommercial.domain.ports.out;

import pe.edu.vallegrande.vgmscommercial.domain.models.Payment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPaymentRepository {
    Mono<Payment> save(Payment payment);
    Mono<Payment> findById(String id);
    Flux<Payment> findByOrganizationId(String organizationId);
    Flux<Payment> findByUserId(String userId);
    Flux<Payment> findByOrganizationIdAndStatus(String organizationId, String status);
    Mono<String> generateReceiptNumber(String organizationId);
    Mono<Long> countByOrganizationIdAndStatus(String organizationId, String status);
}