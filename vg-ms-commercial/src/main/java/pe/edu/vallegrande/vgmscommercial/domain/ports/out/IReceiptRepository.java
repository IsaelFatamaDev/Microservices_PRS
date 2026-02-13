package pe.edu.vallegrande.vgmscommercial.domain.ports.out;

import pe.edu.vallegrande.vgmscommercial.domain.models.Receipt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IReceiptRepository {
    Mono<Receipt> save(Receipt receipt);
    Mono<Receipt> findById(String id);
    Flux<Receipt> findByOrganizationId(String organizationId);
    Flux<Receipt> findByUserId(String userId);
    Flux<Receipt> findByOrganizationIdAndStatus(String organizationId, String status);
    Flux<Receipt> findByOrganizationIdAndPeriod(String organizationId, Integer month, Integer year);
    Mono<Boolean> existsByUserIdAndPeriod(String userId, Integer month, Integer year);
    Mono<String> generateReceiptNumber(String organizationId, Integer year);
    Mono<Long> countByOrganizationIdAndStatus(String organizationId, String status);
}