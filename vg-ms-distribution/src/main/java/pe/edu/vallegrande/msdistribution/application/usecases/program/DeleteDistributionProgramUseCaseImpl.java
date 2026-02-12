package pe.edu.vallegrande.msdistribution.application.usecases.program;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.msdistribution.domain.exceptions.specific.DistributionProgramNotFoundException;
import pe.edu.vallegrande.msdistribution.domain.ports.in.program.IDeleteDistributionProgramUseCase;
import pe.edu.vallegrande.msdistribution.domain.ports.out.program.IDistributionProgramEventPublisher;
import pe.edu.vallegrande.msdistribution.domain.ports.out.program.IDistributionProgramRepository;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DeleteDistributionProgramUseCaseImpl implements IDeleteDistributionProgramUseCase {

    private final IDistributionProgramRepository repository;
    private final IDistributionProgramEventPublisher eventPublisher;

    @Override
    public Mono<Void> execute(String id, String deletedBy, String reason) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new DistributionProgramNotFoundException(id)))
                .flatMap(existing -> {
                    var deleted = existing.markAsDeleted(deletedBy);
                    return repository.update(deleted);
                })
                .flatMap(saved -> eventPublisher.publishProgramDeleted(id, deletedBy, reason))
                .then();
    }
}
