package pe.edu.vallegrande.vgmsorganizations.application.usescases.zone;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.ZoneNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Zone;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.zone.IGetZoneUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.zone.IZoneRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetZoneUseCaseImpl implements IGetZoneUseCase {

    private final IZoneRepository zoneRepository;


    @Override
    public Mono<Zone> findById(String id) {
        log.info("Finding zone by id: {}", id);
        return zoneRepository.findById(id)
            .switchIfEmpty(Mono.error(new ZoneNotFoundException(id)));
    }

    @Override
    public Flux<Zone> findAll() {
        log.info("Finding all zones");
        return zoneRepository.findAll();
    }

    @Override
    public Flux<Zone> findAllActive() {
        log.info("Finding all active zones");
        return zoneRepository.findByRecordStatus(RecordStatus.ACTIVE);
    }

    @Override
    public Flux<Zone> findByOrganizationId(String organizationId) {
        log.info("Finding zones by organization id: {}", organizationId);
        return zoneRepository.findByOrganizationId(organizationId);
    }

    @Override
    public Flux<Zone> findActiveByOrganizationId(String organizationId) {
        log.info("Finding active zones by organization id: {}", organizationId);
        return zoneRepository.findByOrganizationIdAndRecordStatus(organizationId, RecordStatus.ACTIVE);
    }
}
