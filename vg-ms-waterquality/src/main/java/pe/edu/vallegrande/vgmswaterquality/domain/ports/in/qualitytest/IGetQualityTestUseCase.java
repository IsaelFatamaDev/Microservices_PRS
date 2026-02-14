package pe.edu.vallegrande.vgmswaterquality.domain.ports.in.qualitytest;

import pe.edu.vallegrande.vgmswaterquality.domain.models.QualityTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetQualityTestUseCase {
    Mono<QualityTest> findByAll(String id);

    Flux<QualityTest> findAll();

    Flux<QualityTest> findAllActive();

    Flux<QualityTest> findByOrganizationId(String organizationId);

    Flux<QualityTest> findActiveByOrganizationId(String organizationId);
}
