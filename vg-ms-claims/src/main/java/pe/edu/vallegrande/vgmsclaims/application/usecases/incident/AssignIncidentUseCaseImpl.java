package pe.edu.vallegrande.vgmsclaims.application.usecases.incident;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.base.BusinessRuleException;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific.IncidentNotFoundException;
import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.incident.IAssignIncidentUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IClaimsEventPublisher;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IIncidentRepository;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IUserServiceClient;
import pe.edu.vallegrande.vgmsclaims.application.events.incident.IncidentAssignedEvent;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Use case implementation for assigning incidents
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssignIncidentUseCaseImpl implements IAssignIncidentUseCase {
    
    private final IIncidentRepository incidentRepository;
    private final IClaimsEventPublisher eventPublisher;
    private final IUserServiceClient userServiceClient;
    
    @Override
    public Mono<Incident> execute(String incidentId, String userId) {
        log.info("Assigning incident {} al user {}", incidentId, userId);
        
        return incidentRepository.findById(incidentId)
                .switchIfEmpty(Mono.error(new IncidentNotFoundException(incidentId)))
                .flatMap(incident -> {
                    if (!incident.canBeAssigned()) {
                        return Mono.error(new BusinessRuleException(
                                "INVALID_STATE",
                                "The incident cannot be assigned in its current state"));
                    }
                    
                    // Checksr que el user existe
                    return userServiceClient.existsUser(userId)
                            .flatMap(exists -> {
                                if (!exists) {
                                    return Mono.error(new BusinessRuleException(
                                            "USER_NOT_FOUND",
                                            "The assigned user does not exist"));
                                }
                                
                                incident.assignTo(userId);
                                return incidentRepository.save(incident);
                            });
                })
                .doOnSuccess(saved -> {
                    log.info("Incident assigned: {} -> {}", saved.getIncidentCode(), userId);
                    publishIncidentAssignedEvent(saved);
                })
                .doOnError(error -> log.error("Error assigning incident: {}", error.getMessage()));
    }
    
    private void publishIncidentAssignedEvent(Incident incident) {
        try {
            IncidentAssignedEvent event = IncidentAssignedEvent.builder()
                    .incidentId(incident.getId())
                    .incidentCode(incident.getIncidentCode())
                    .assignedToUserId(incident.getAssignedToUserId())
                    .severity(incident.getSeverity() != null ? incident.getSeverity().name() : null)
                    .assignedAt(Instant.now())
                    .build();
            eventPublisher.publishIncidentAssigned(event);
        } catch (Exception e) {
            log.warn("Could not publish incident assigned event: {}", e.getMessage());
        }
    }
}
