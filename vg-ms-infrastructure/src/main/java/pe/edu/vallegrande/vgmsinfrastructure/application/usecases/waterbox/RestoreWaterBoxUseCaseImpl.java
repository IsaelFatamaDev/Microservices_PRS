package pe.edu.vallegrande.vgmsinfrastructure.application.usecases.waterbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.BusinessRuleException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.WaterBoxNotFoundException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBox;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.IRestoreWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IInfrastructureEventPublisher;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IWaterBoxRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreWaterBoxUseCaseImpl implements IRestoreWaterBoxUseCase {

    private final IWaterBoxRepository repository;
    private final IInfrastructureEventPublisher eventPublisher;

    @Override
    public Mono<WaterBox> execute(String id, String restoredBy) {
        log.info("Restoring water box: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new WaterBoxNotFoundException(id)))
                .flatMap(waterBox -> {
                    if (waterBox.getRecordStatus() == RecordStatus.ACTIVE) {
                        return Mono.error(new BusinessRuleException("Water box is already active"));
                    }
                    return Mono.just(waterBox.restore(restoredBy));
                })
                .flatMap(repository::save)
                .flatMap(saved -> eventPublisher.publishWaterBoxRestored(saved.getId(), restoredBy)
                        .thenReturn(saved))
                .doOnSuccess(saved -> log.info("Water box restored: {}", saved.getId()))
                .doOnError(error -> log.error("Error restoring water box {}: {}", id, error.getMessage()));
    }
}
