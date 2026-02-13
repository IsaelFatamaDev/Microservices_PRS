package pe.edu.vallegrande.vgmsinfrastructure.application.usecases.waterbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.BusinessRuleException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.WaterBoxNotFoundException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBox;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.IReconnectWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IInfrastructureEventPublisher;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IWaterBoxRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReconnectWaterBoxUseCaseImpl implements IReconnectWaterBoxUseCase {

    private final IWaterBoxRepository repository;
    private final IInfrastructureEventPublisher eventPublisher;

    @Override
    public Mono<WaterBox> execute(String id, String reconnectedBy) {
        log.info("Reconnecting water box: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new WaterBoxNotFoundException(id)))
                .flatMap(waterBox -> {
                    if (waterBox.isServiceActive()) {
                        return Mono.error(new BusinessRuleException("Water box is already active"));
                    }
                    return Mono.just(waterBox.reconnect(reconnectedBy));
                })
                .flatMap(repository::save)
                .flatMap(saved -> eventPublisher.publishWaterBoxReconnected(saved.getId(), reconnectedBy)
                        .thenReturn(saved))
                .doOnSuccess(saved -> log.info("Water box reconnected: {}", saved.getId()))
                .doOnError(error -> log.error("Error reconnecting water box {}: {}", id, error.getMessage()));
    }
}
