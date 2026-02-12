package pe.edu.vallegrande.msdistribution.application.usecases.program;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.msdistribution.domain.exceptions.specific.DistributionProgramNotFoundException;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionProgram;
import pe.edu.vallegrande.msdistribution.domain.ports.in.program.IUpdateDistributionProgramUseCase;
import pe.edu.vallegrande.msdistribution.domain.ports.out.program.IDistributionProgramEventPublisher;
import pe.edu.vallegrande.msdistribution.domain.ports.out.program.IDistributionProgramRepository;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UpdateDistributionProgramUseCaseImpl implements IUpdateDistributionProgramUseCase {

    private final IDistributionProgramRepository repository;
    private final IDistributionProgramEventPublisher eventPublisher;

    @Override
    public Mono<DistributionProgram> execute(String id, DistributionProgram changes, String updatedBy) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new DistributionProgramNotFoundException(id)))
                .flatMap(existing -> {
                    DistributionProgram updated = existing.updateWith(changes, updatedBy);
                    Map<String, Object> changedFields = new HashMap<>();
                    return repository.update(updated)
                            .flatMap(saved -> eventPublisher.publishProgramUpdated(saved, changedFields, updatedBy)
                                    .thenReturn(saved));
                });
    }
}
