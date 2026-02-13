package pe.edu.vallegrande.vgmsclaims.application.usecases.incident;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.base.BusinessRuleException;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific.IncidentNotFoundException;
import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.IncidentStatus;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.incident.ICloseIncidentUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IClaimsEventPublisher;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IIncidentRepository;
import pe.edu.vallegrande.vgmsclaims.application.events.incident.IncidentClosedEvent;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Use case implementation for closing incidents
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CloseIncidentUseCaseImpl implements ICloseIncidentUseCase {
    
    private final IIncidentRepository incidentRepository;
    private final IClaimsEventPublisher eventPublisher;
    
    @Override
    public Mono<Incident> execute(String id) {
        log.info("Closing incident with ID: {}", id);
        
        return incidentRepository.findById(id)
                .switchIfEmpty(Mono.error(new IncidentNotFoundException(id)))
                .flatMap(incident -> {
                    if (incident.getStatus() == IncidentStatus.CLOSED) {
                        return Mono.error(new BusinessRuleException(
                                "ALREADY_CLOSED",
                                "The incident is already closed"));
                    }
                    
                    if (!incident.canBeClosed()) {
                        return Mono.error(new BusinessRuleException(
                                "INVALID_STATE",
                                "The incident cannot be closed in its current state"));
                    }
                    
                    incident.close();
                    return incidentRepository.save(incident);
                })
                .doOnSuccess(saved -> {
                    log.info("Incident closed: {}", saved.getIncidentCode());
                    publishIncidentClosedEvent(saved);
                })
                .doOnError(error -> log.error("Error closing incident: {}", error.getMessage()));
    }
    
    private void publishIncidentClosedEvent(Incident incident) {
        try {
            IncidentClosedEvent event = IncidentClosedEvent.builder()
                    .incidentId(incident.getId())
                    .incidentCode(incident.getIncidentCode())
                    .closedAt(Instant.now())
                    .build();
            eventPublisher.publishIncidentClosed(event);
        } catch (Exception e) {
            log.warn("Could not publish incident closed event: {}", e.getMessage());
        }
    }
}
