package pe.edu.vallegrande.msdistribution.domain.ports.out.program;

import pe.edu.vallegrande.msdistribution.domain.models.DistributionProgram;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IDistributionProgramRepository {
    Mono<DistributionProgram> save(DistributionProgram program);
    Mono<DistributionProgram> findById(String id);
    Mono<DistributionProgram> update(DistributionProgram program);
    Flux<DistributionProgram> findAll();
    Flux<DistributionProgram> findByRecordStatus(RecordStatus status);
    Flux<DistributionProgram> findByOrganizationId(String organizationId);
    Mono<Void> deleteById(String id);
}
