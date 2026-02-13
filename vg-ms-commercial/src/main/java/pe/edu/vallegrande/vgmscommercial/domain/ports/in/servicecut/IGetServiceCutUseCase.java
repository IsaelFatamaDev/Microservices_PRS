package pe.edu.vallegrande.vgmscommercial.domain.ports.in.servicecut;

import pe.edu.vallegrande.vgmscommercial.domain.models.ServiceCut;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetServiceCutUseCase {
    Mono<ServiceCut> findById(String id, String organizationId);
    Flux<ServiceCut> findAll(String organizationId, String status, String userId, Integer page, Integer size);
    Flux<ServiceCut> findByUserId(String userId, String organizationId);
    Flux<ServiceCut> findPending(String organizationId);
    Mono<Long> count(String organizationId, String status);
}