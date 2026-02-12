package pe.edu.vallegrande.msdistribution.application.usecases.program;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionProgram;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.msdistribution.domain.ports.in.program.ICreateDistributionProgramUseCase;
import pe.edu.vallegrande.msdistribution.domain.ports.out.program.IDistributionProgramEventPublisher;
import pe.edu.vallegrande.msdistribution.domain.ports.out.program.IDistributionProgramRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateDistributionProgramUseCaseImpl implements ICreateDistributionProgramUseCase {

    private final IDistributionProgramRepository repository;
    private final IDistributionProgramEventPublisher eventPublisher;

    @Override
    public Mono<DistributionProgram> execute(DistributionProgram program, String createdBy) {
        LocalDateTime now = LocalDateTime.now();
        DistributionProgram newProgram = program.toBuilder()
                .recordStatus(RecordStatus.ACTIVE)
                .createdAt(now)
                .createdBy(createdBy)
                .updatedAt(now)
                .updatedBy(createdBy)
                .build();
        return repository.save(newProgram)
                .flatMap(saved -> eventPublisher.publishProgramCreated(saved, createdBy)
                        .thenReturn(saved));
    }
}
