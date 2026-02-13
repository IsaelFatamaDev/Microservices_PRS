package pe.edu.vallegrande.vgmsclaims.application.usecases.incident;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific.IncidentNotFoundException;
import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.incident.IUpdateIncidentUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IClaimsEventPublisher;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IIncidentRepository;
import pe.edu.vallegrande.vgmsclaims.application.events.incident.IncidentUpdatedEvent;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Use case implementation for updating incidents
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateIncidentUseCaseImpl implements IUpdateIncidentUseCase {

    private final IIncidentRepository incidentRepository;
    private final IClaimsEventPublisher eventPublisher;

    @Override
    public Mono<Incident> execute(String id, Incident updatedData) {
        log.info("Updating incident with ID: {}", id);

        return incidentRepository.findById(id)
                .switchIfEmpty(Mono.error(new IncidentNotFoundException(id)))
                .flatMap(existing -> {
                    // Update allowed fields
                    if (updatedData.getTitle() != null) {
                        existing.setTitle(updatedData.getTitle());
                    }
                    if (updatedData.getDescription() != null) {
                        existing.setDescription(updatedData.getDescription());
                    }
                    if (updatedData.getSeverity() != null) {
                        existing.setSeverity(updatedData.getSeverity());
                    }
                    if (updatedData.getStatus() != null) {
                        existing.setStatus(updatedData.getStatus());
                    }
                    if (updatedData.getAffectedBoxesCount() != null) {
                        existing.setAffectedBoxesCount(updatedData.getAffectedBoxesCount());
                    }
                    if (updatedData.getResolutionNotes() != null) {
                        existing.setResolutionNotes(updatedData.getResolutionNotes());
                    }

                    existing.setUpdatedAt(Instant.now());

                    return incidentRepository.save(existing);
                })
                .doOnSuccess(saved -> {
                    log.info("Incident updated: {}", saved.getIncidentCode());
                    publishIncidentUpdatedEvent(saved);
                })
                .doOnError(error -> log.error("Error updating incident: {}", error.getMessage()));
    }

    private void publishIncidentUpdatedEvent(Incident incident) {
        try {
            IncidentUpdatedEvent event = IncidentUpdatedEvent.builder()
                    .incidentId(incident.getId())
                    .incidentCode(incident.getIncidentCode())
                    .status(incident.getStatus() != null ? incident.getStatus().name() : null)
                    .severity(incident.getSeverity() != null ? incident.getSeverity().name() : null)
                    .updatedAt(incident.getUpdatedAt())
                    .build();
            eventPublisher.publishIncidentUpdated(event);
        } catch (Exception e) {
            log.warn("Could not publish incident updated event: {}", e.getMessage());
        }
    }
}
