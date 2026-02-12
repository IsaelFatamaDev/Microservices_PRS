package pe.edu.vallegrande.msdistribution.domain.ports.in.program;

import pe.edu.vallegrande.msdistribution.domain.models.DistributionProgram;
import reactor.core.publisher.Mono;

public interface ICreateDistributionProgramUseCase {
    Mono<DistributionProgram> execute(DistributionProgram program, String createdBy);
}
