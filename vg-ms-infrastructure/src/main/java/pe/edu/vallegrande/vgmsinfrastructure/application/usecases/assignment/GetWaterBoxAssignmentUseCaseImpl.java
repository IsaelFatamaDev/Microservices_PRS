package pe.edu.vallegrande.vgmsinfrastructure.application.usecases.assignment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.AssignmentNotFoundException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBoxAssignment;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.IGetWaterBoxAssignmentUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IWaterBoxAssignmentRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetWaterBoxAssignmentUseCaseImpl implements IGetWaterBoxAssignmentUseCase {

    private final IWaterBoxAssignmentRepository repository;

    @Override
    public Mono<WaterBoxAssignment> findById(String id) {
        log.debug("Finding assignment by ID: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new AssignmentNotFoundException(id)));
    }

    @Override
    public Flux<WaterBoxAssignment> findAll() {
        log.debug("Finding all assignments");
        return repository.findAll();
    }

    @Override
    public Flux<WaterBoxAssignment> findByWaterBoxId(String waterBoxId) {
        log.debug("Finding assignments by water box: {}", waterBoxId);
        return repository.findByWaterBoxId(waterBoxId);
    }

    @Override
    public Flux<WaterBoxAssignment> findByUserId(String userId) {
        log.debug("Finding assignments by user: {}", userId);
        return repository.findByUserId(userId);
    }
}
