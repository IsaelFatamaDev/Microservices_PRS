package pe.edu.vallegrande.msdistribution.domain.ports.out.program;

import pe.edu.vallegrande.msdistribution.domain.models.DistributionProgram;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface IDistributionProgramEventPublisher {
    Mono<Void> publishProgramCreated(DistributionProgram program, String createdBy);
    Mono<Void> publishProgramUpdated(DistributionProgram program, Map<String, Object> changedFields, String updatedBy);
    Mono<Void> publishProgramDeleted(String programId, String deletedBy, String reason);
    Mono<Void> publishProgramRestored(String programId, String restoredBy);
}
