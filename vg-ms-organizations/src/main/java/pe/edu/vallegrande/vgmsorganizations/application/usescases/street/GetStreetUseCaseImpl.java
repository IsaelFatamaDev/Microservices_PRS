package pe.edu.vallegrande.vgmsorganizations.application.usescases.street;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.StreetNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Street;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.street.IGetStreetUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.street.IStreetRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetStreetUseCaseImpl implements IGetStreetUseCase {

    private final IStreetRepository streetRepository;

    @Override
    public Mono<Street> findById(String id) {
        return streetRepository.findById(id)
            .switchIfEmpty(Mono.error(new StreetNotFoundException(id)));
    }

    @Override
    public Flux<Street> findAllActive() {
        return streetRepository.findByRecordStatus(RecordStatus.ACTIVE);
    }

    @Override
    public Flux<Street> findAll() {
        return streetRepository.findAll();
    }

    @Override
    public Flux<Street> findByZoneId(String zoneId) {
        return streetRepository.findByZoneId(zoneId);
    }

    @Override
    public Flux<Street> findActiveByZoneId(String zoneId) {
        return streetRepository.findByZoneIdAndRecordStatus(zoneId, RecordStatus.ACTIVE);
    }
}
