package pe.edu.vallegrande.vgmsclaims.application.usecases.incident;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.base.BusinessRuleException;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific.IncidentAlreadyResolvedException;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific.IncidentNotFoundException;
import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentResolution;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.incident.IResolveIncidentUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IClaimsEventPublisher;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IIncidentRepository;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.ISecurityContext;
import pe.edu.vallegrande.vgmsclaims.application.events.incident.IncidentResolvedEvent;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Use case implementation for resolving incidents
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResolveIncidentUseCaseImpl implements IResolveIncidentUseCase {
    
    private final IIncidentRepository incidentRepository;
    private final IClaimsEventPublisher eventPublisher;
    private final ISecurityContext securityContext;
    
    @Override
    public Mono<Incident> execute(String incidentId, IncidentResolution resolution) {
        log.info("Resolving incident: {}", incidentId);
        
        return securityContext.getCurrentUserId()
                .flatMap(userId -> incidentRepository.findById(incidentId)
                        .switchIfEmpty(Mono.error(new IncidentNotFoundException(incidentId)))
                        .flatMap(incident -> {
                            if (incident.getResolved() != null && incident.getResolved()) {
                                return Mono.error(new IncidentAlreadyResolvedException(incidentId));
                            }
                            
                            if (!incident.canBeResolved()) {
                                return Mono.error(new BusinessRuleException(
                                        "INVALID_STATE",
                                        "The incident cannot be resolved in its current state"));
                            }
                            
                            incident.resolve(userId, resolution.getResolutionNotes());
                            return incidentRepository.save(incident);
                        }))
                .doOnSuccess(saved -> {
                    log.info("Incident resolved: {}", saved.getIncidentCode());
                    publishIncidentResolvedEvent(saved, resolution);
                })
                .doOnError(error -> log.error("Error resolving incident: {}", error.getMessage()));
    }
    
    private void publishIncidentResolvedEvent(Incident incident, IncidentResolution resolution) {
        try {
            IncidentResolvedEvent event = IncidentResolvedEvent.builder()
                    .incidentId(incident.getId())
                    .incidentCode(incident.getIncidentCode())
                    .resolvedByUserId(incident.getResolvedByUserId())
                    .resolutionType(resolution.getResolutionType() != null ? 
                            resolution.getResolutionType().name() : null)
                    .totalCost(resolution.getTotalCost())
                    .resolvedAt(Instant.now())
                    .build();
            eventPublisher.publishIncidentResolved(event);
        } catch (Exception e) {
            log.warn("Could not publish incident resolved event: {}", e.getMessage());
        }
    }
}
