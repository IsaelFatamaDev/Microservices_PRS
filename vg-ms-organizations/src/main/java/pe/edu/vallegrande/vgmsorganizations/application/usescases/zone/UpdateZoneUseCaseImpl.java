package pe.edu.vallegrande.vgmsorganizations.application.usescases.zone;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.ZoneNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Zone;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.zone.IUpdateZoneUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.zone.IZoneEventPublisher;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.zone.IZoneRepository;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateZoneUseCaseImpl implements IUpdateZoneUseCase {

    private final IZoneRepository zoneRepository;
    private final IZoneEventPublisher eventPublisher;

    @Override
    public Mono<Zone> execute(String id, Zone changes, String updatedBy) {
        log.info("Updating zone: {}", id);

        return zoneRepository.findById(id)
            .switchIfEmpty(Mono.error(new ZoneNotFoundException(id)))
            .flatMap(existing -> {
                Map<String, Object> changedFields = detectChanges(existing, changes);
                if (changedFields.isEmpty()) {
                    return Mono.just(existing);
                }

                Zone updated = existing.updateWith(changes, updatedBy);
                return zoneRepository.update(updated)
                    .flatMap(saved -> eventPublisher.publishZoneUpdated(saved, changedFields, updatedBy)
                        .thenReturn(saved)
                        .onErrorResume(e -> {
                            log.warn("Failed to publish zone update event: {}", e.getMessage());
                            return Mono.just(saved);
                        })
                    );
            });
    }

    private Map<String, Object> detectChanges(Zone existing, Zone changes){
        Map<String, Object> changedFields = new HashMap<>();
        if(changes.getZoneName() != null && !changes.getZoneName().equals(existing.getZoneName())){
            changedFields.put("zoneName", changes.getZoneName());
        }
        if(changes.getDescription() != null && !changes.getDescription().equals(existing.getDescription())){
            changedFields.put("description", changes.getDescription());
        }
        return changedFields;
    }

}
