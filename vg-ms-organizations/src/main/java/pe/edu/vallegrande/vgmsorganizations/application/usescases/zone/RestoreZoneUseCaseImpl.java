package pe.edu.vallegrande.vgmsorganizations.application.usescases.zone;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base.BusinessRuleException;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.ZoneNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Zone;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.zone.IRestoreZoneUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.zone.IZoneEventPublisher;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.zone.IZoneRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreZoneUseCaseImpl implements IRestoreZoneUseCase {

    private final IZoneRepository zoneRepository;
    private final IZoneEventPublisher eventPublisher;

    @Override
    public Mono<Zone> execute(String id, String restoredBy) {
        log.info("Restoring zone: {}", id);

        return zoneRepository.findById(id)
            .switchIfEmpty(Mono.error(new ZoneNotFoundException(id)))
            .flatMap(zone -> {
                if (zone.isActive()) {
                    return Mono.error(new BusinessRuleException("Zone is already active"));
                }

                Zone restored = zone.restore(restoredBy);
                return zoneRepository.update(restored)
                    .flatMap(saved -> eventPublisher.publishZoneRestored(saved.getId(), saved.getOrganizationId(), restoredBy)
                        .thenReturn(saved)
                        .onErrorResume(e -> {
                            log.warn("Failed to publish zone restore event: {}", e.getMessage());
                            return Mono.just(saved);
                        })
                    );
            });
    }
}