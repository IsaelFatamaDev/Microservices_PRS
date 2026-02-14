package pe.edu.vallegrande.vgmswaterquality.domain.ports.in.testingpoint;

import pe.edu.vallegrande.vgmswaterquality.domain.models.TestingPoint;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetTestingPointUseCase {
    Mono<TestingPoint> findByAll(String id);

    Flux<TestingPoint> findAll();

    Flux<TestingPoint> findAllActive();

    Flux<TestingPoint> findByOrganizationId(String organizationId);

    Flux<TestingPoint> findActiveByOrganizationId(String organizationId);
}
