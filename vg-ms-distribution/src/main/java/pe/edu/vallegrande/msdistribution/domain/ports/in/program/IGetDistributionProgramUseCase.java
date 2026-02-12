package pe.edu.vallegrande.msdistribution.domain.ports.in.program;

import pe.edu.vallegrande.msdistribution.domain.models.DistributionProgram;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetDistributionProgramUseCase {
    Mono<DistributionProgram> findById(String id);
    Flux<DistributionProgram> findAll();
    Flux<DistributionProgram> findAllActive();
}
