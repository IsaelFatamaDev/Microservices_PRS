package pe.edu.vallegrande.msdistribution.domain.ports.out.route;

import pe.edu.vallegrande.msdistribution.domain.models.DistributionRoute;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IDistributionRouteRepository {
    Mono<DistributionRoute> save(DistributionRoute route);
    Mono<DistributionRoute> findById(String id);
    Mono<DistributionRoute> update(DistributionRoute route);
    Flux<DistributionRoute> findAll();
    Flux<DistributionRoute> findByRecordStatus(RecordStatus status);
    Flux<DistributionRoute> findByOrganizationId(String organizationId);
    Mono<Void> deleteById(String id);
}
