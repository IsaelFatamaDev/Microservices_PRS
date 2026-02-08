package pe.edu.vallegrande.vgmsorganizations.application.usescases.zone;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base.BussinessRuleException;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.ZoneNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Zone;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.zone.IDeleteZoneUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.zone.IZoneEventPublisher;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.zone.IZoneRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteZoneUseCaseImpl implements IDeleteZoneUseCase {

    private final IZoneRepository zoneRepository;
    private final IZoneEventPublisher eventPublisher;

    @Override
    public Mono<Zone> execute(String id, String deletedBy, String reason) {
        log.info("Deleting zone: {} - Reason: {}", id, reason);

        return zoneRepository.findById(id)
            .switchIfEmpty(Mono.error(new ZoneNotFoundException(id)))
            .flatMap(zone -> {
                if (zone.isInactive()) {
                    return Mono.error(new BussinessRuleException("Zone is already inactive"));
                }

                Zone deleted = zone.markAsDeleted(deletedBy);
                return zoneRepository.update(deleted)
                    .flatMap(saved -> eventPublisher.publishZoneDeleted(saved.getId(), saved.getOrganizationId(), reason, deletedBy)
                        .thenReturn(saved)
                        .onErrorResume(e -> {
                            log.warn("Failed to publish zone delete event: {}", e.getMessage());
                            return Mono.just(saved);
                        })
                    );
            });
    }
}