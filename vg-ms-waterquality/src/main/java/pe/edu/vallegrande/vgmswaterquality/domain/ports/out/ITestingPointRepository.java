package pe.edu.vallegrande.vgmswaterquality.domain.ports.out;

import pe.edu.vallegrande.vgmswaterquality.domain.models.TestingPoint;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITestingPointRepository {
    Mono<TestingPoint> save(TestingPoint point);

    Mono<TestingPoint> update(TestingPoint update);

    Mono<TestingPoint> findById(String id);

    Flux<TestingPoint> findByOrganizationId(String organizationId);

    Flux<TestingPoint> findAll();

    Flux<TestingPoint> findByRecordStatus(String recordStatus);

    Flux<TestingPoint> findByOrganizationIdAndRecordStatus(String organizationId, String recordStatus);

    Mono<Void> deleteById(String id);
}
