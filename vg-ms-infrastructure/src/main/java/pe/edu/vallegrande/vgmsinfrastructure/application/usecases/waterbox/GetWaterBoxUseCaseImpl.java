package pe.edu.vallegrande.vgmsinfrastructure.application.usecases.waterbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.WaterBoxNotFoundException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBox;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.IGetWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IWaterBoxRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetWaterBoxUseCaseImpl implements IGetWaterBoxUseCase {

    private final IWaterBoxRepository repository;

    @Override
    public Mono<WaterBox> findById(String id) {
        log.debug("Finding water box by ID: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new WaterBoxNotFoundException(id)));
    }

    @Override
    public Flux<WaterBox> findAll() {
        log.debug("Finding all water boxes");
        return repository.findAll();
    }

    @Override
    public Flux<WaterBox> findAllActive() {
        log.debug("Finding all active water boxes");
        return repository.findByRecordStatus(RecordStatus.ACTIVE);
    }

    @Override
    public Flux<WaterBox> findByZoneId(String zoneId) {
        log.debug("Finding water boxes by zone: {}", zoneId);
        return repository.findByZoneId(zoneId);
    }
}
