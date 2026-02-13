package pe.edu.vallegrande.vgmsinfrastructure.application.usecases.waterbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.WaterBoxNotFoundException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.IDeleteWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IInfrastructureEventPublisher;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IWaterBoxRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteWaterBoxUseCaseImpl implements IDeleteWaterBoxUseCase {

    private final IWaterBoxRepository repository;
    private final IInfrastructureEventPublisher eventPublisher;

    @Override
    public Mono<Void> execute(String id, String deletedBy) {
        log.info("Soft deleting water box: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new WaterBoxNotFoundException(id)))
                .map(waterBox -> waterBox.markAsDeleted(deletedBy))
                .flatMap(repository::save)
                .flatMap(saved -> eventPublisher.publishWaterBoxDeleted(saved.getId(), deletedBy))
                .doOnSuccess(v -> log.info("Water box soft deleted: {}", id))
                .doOnError(error -> log.error("Error deleting water box {}: {}", id, error.getMessage()));
    }
}
