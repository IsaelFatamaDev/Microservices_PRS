package pe.edu.vallegrande.vgmsinfrastructure.application.usecases.waterbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.WaterBoxNotFoundException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBox;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.IUpdateWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IInfrastructureEventPublisher;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IWaterBoxRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateWaterBoxUseCaseImpl implements IUpdateWaterBoxUseCase {

    private final IWaterBoxRepository repository;
    private final IInfrastructureEventPublisher eventPublisher;

    @Override
    public Mono<WaterBox> execute(String id, WaterBox changes, String updatedBy) {
        log.info("Updating water box: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new WaterBoxNotFoundException(id)))
                .map(existing -> existing.updateWith(changes, updatedBy))
                .flatMap(repository::save)
                .flatMap(saved -> eventPublisher.publishWaterBoxUpdated(saved.getId(), updatedBy)
                        .thenReturn(saved))
                .doOnSuccess(saved -> log.info("Water box updated successfully: {}", saved.getId()))
                .doOnError(error -> log.error("Error updating water box {}: {}", id, error.getMessage()));
    }
}
