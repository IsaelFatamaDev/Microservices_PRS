package pe.edu.vallegrande.vgmsinfrastructure.application.usecases.waterbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.WaterBoxAlreadySuspendedException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.WaterBoxNotFoundException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBox;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.ISuspendWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IInfrastructureEventPublisher;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IWaterBoxRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuspendWaterBoxUseCaseImpl implements ISuspendWaterBoxUseCase {

    private final IWaterBoxRepository repository;
    private final IInfrastructureEventPublisher eventPublisher;

    @Override
    public Mono<WaterBox> execute(String id, String suspendedBy) {
        log.info("Suspending water box: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new WaterBoxNotFoundException(id)))
                .flatMap(waterBox -> {
                    if (!waterBox.isServiceActive()) {
                        return Mono.error(new WaterBoxAlreadySuspendedException(id));
                    }
                    return Mono.just(waterBox.suspend(suspendedBy));
                })
                .flatMap(repository::save)
                .flatMap(saved -> eventPublisher.publishWaterBoxSuspended(saved.getId(), suspendedBy)
                        .thenReturn(saved))
                .doOnSuccess(saved -> log.info("Water box suspended: {}", saved.getId()))
                .doOnError(error -> log.error("Error suspending water box {}: {}", id, error.getMessage()));
    }
}
