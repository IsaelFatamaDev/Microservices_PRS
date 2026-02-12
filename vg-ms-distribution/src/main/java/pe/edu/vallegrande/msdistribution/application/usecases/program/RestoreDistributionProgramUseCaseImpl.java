package pe.edu.vallegrande.msdistribution.application.usecases.program;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.msdistribution.domain.exceptions.specific.DistributionProgramNotFoundException;
import pe.edu.vallegrande.msdistribution.domain.ports.in.program.IRestoreDistributionProgramUseCase;
import pe.edu.vallegrande.msdistribution.domain.ports.out.program.IDistributionProgramEventPublisher;
import pe.edu.vallegrande.msdistribution.domain.ports.out.program.IDistributionProgramRepository;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestoreDistributionProgramUseCaseImpl implements IRestoreDistributionProgramUseCase {

    private final IDistributionProgramRepository repository;
    private final IDistributionProgramEventPublisher eventPublisher;

    @Override
    public Mono<Void> execute(String id, String restoredBy) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new DistributionProgramNotFoundException(id)))
                .flatMap(existing -> {
                    var restored = existing.restore(restoredBy);
                    return repository.update(restored);
                })
                .flatMap(saved -> eventPublisher.publishProgramRestored(id, restoredBy))
                .then();
    }
}
