package pe.edu.vallegrande.vgmscommercial.domain.ports.out;

import pe.edu.vallegrande.vgmscommercial.domain.models.ServiceCut;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IServiceCutRepository {
    Mono<ServiceCut> save(ServiceCut serviceCut);
    Mono<ServiceCut> findById(String id);
    Flux<ServiceCut> findByOrganizationId(String organizationId);
    Flux<ServiceCut> findByUserId(String userId);
    Flux<ServiceCut> findByOrganizationIdAndStatus(String organizationId, String status);
    Flux<ServiceCut> findPendingByOrganizationId(String organizationId);
    Mono<Boolean> existsActiveByUserId(String userId);
    Mono<Long> countByOrganizationIdAndStatus(String organizationId, String status);
}