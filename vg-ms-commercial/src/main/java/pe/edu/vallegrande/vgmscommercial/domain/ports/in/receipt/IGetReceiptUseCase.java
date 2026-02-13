package pe.edu.vallegrande.vgmscommercial.domain.ports.in.receipt;

import pe.edu.vallegrande.vgmscommercial.domain.models.Receipt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetReceiptUseCase {
    Mono<Receipt> findById(String id, String organizationId);
    Flux<Receipt> findAll(String organizationId, String status, String userId, Integer page, Integer size);
    Flux<Receipt> findByUserId(String userId, String organizationId);
    Flux<Receipt> findByPeriod(Integer month, Integer year, String organizationId);
    Mono<Long> count(String organizationId, String status);
}