package pe.edu.vallegrande.vgmswaterquality.domain.ports.out;

import pe.edu.vallegrande.vgmswaterquality.domain.models.QualityTest;
import pe.edu.vallegrande.vgmswaterquality.domain.models.valueobjects.RecordStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IQualityTestRepository {
    Mono<QualityTest> save(QualityTest test);

    Mono<QualityTest> update(QualityTest test);

    Mono<QualityTest> findById(String id);

    Flux<QualityTest> findByOrganizationId(String organizationId);

    Flux<QualityTest> findAll();

    Flux<QualityTest> findByRecordStatus(String recordStatus);

    Flux<QualityTest> findByOrganizationIdAndRecordStatus(String organizationId , RecordStatus recordStatus);

    Mono<Void> deleteById(String id);


}
