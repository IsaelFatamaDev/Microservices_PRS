package pe.edu.vallegrande.msdistribution.domain.ports.in.program;

import pe.edu.vallegrande.msdistribution.domain.models.DistributionProgram;
import reactor.core.publisher.Mono;

public interface IUpdateDistributionProgramUseCase {
    Mono<DistributionProgram> execute(String id, DistributionProgram program, String updatedBy);
}
