package pe.edu.vallegrande.msdistribution.application.usecases.program;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.msdistribution.domain.exceptions.specific.DistributionProgramNotFoundException;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionProgram;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.msdistribution.domain.ports.in.program.IGetDistributionProgramUseCase;
import pe.edu.vallegrande.msdistribution.domain.ports.out.program.IDistributionProgramRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetDistributionProgramUseCaseImpl implements IGetDistributionProgramUseCase {

    private final IDistributionProgramRepository repository;

    @Override
    public Mono<DistributionProgram> findById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new DistributionProgramNotFoundException(id)));
    }

    @Override
    public Flux<DistributionProgram> findAll() {
        return repository.findAll();
    }

    @Override
    public Flux<DistributionProgram> findAllActive() {
        return repository.findByRecordStatus(RecordStatus.ACTIVE);
    }
}
