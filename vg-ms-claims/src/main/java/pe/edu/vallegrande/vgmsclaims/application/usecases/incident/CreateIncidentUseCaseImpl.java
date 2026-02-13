package pe.edu.vallegrande.vgmsclaims.application.usecases.incident;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.IncidentStatus;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.incident.ICreateIncidentUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IClaimsEventPublisher;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IIncidentRepository;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.ISecurityContext;
import pe.edu.vallegrande.vgmsclaims.application.events.incident.IncidentCreatedEvent;
import pe.edu.vallegrande.vgmsclaims.application.events.incident.UrgentIncidentAlertEvent;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

/**
 * Use case implementation for creating incidents
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateIncidentUseCaseImpl implements ICreateIncidentUseCase {
    
    private final IIncidentRepository incidentRepository;
    private final IClaimsEventPublisher eventPublisher;
    private final ISecurityContext securityContext;
    
    @Override
    public Mono<Incident> execute(Incident incident) {
        log.info("Creando new incident: {}", incident.getTitle());
        
        return securityContext.getCurrentUserId()
                .defaultIfEmpty("system")
                .flatMap(userId -> {
                    // Set default values only if not defined
                    if (incident.getIncidentCode() == null || incident.getIncidentCode().isEmpty()) {
                        incident.setIncidentCode(generateIncidentCode());
                    }
                    if (incident.getReportedByUserId() == null || incident.getReportedByUserId().isEmpty()) {
                        incident.setReportedByUserId(userId);
                    }
                    if (incident.getStatus() == null) {
                        incident.setStatus(IncidentStatus.REPORTED);
                    }
                    if (incident.getRecordStatus() == null) {
                        incident.setRecordStatus(RecordStatus.ACTIVE);
                    }
                    if (incident.getResolved() == null) {
                        incident.setResolved(false);
                    }
                    if (incident.getCreatedAt() == null) {
                        incident.setCreatedAt(Instant.now());
                    }
                    if (incident.getIncidentDate() == null) {
                        incident.setIncidentDate(Instant.now());
                    }
                    
                    return incidentRepository.save(incident)
                            .doOnSuccess(saved -> {
                                log.info("Incident created successfully: {}", saved.getIncidentCode());
                                publishIncidentCreatedEvent(saved);
                                
                                // Alert if critical
                                if (saved.isCritical()) {
                                    publishUrgentIncidentAlert(saved);
                                }
                            })
                            .doOnError(error -> log.error("Error creating incident: {}", error.getMessage()));
                });
    }
    
    private String generateIncidentCode() {
        return "INC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private void publishIncidentCreatedEvent(Incident incident) {
        try {
            IncidentCreatedEvent event = IncidentCreatedEvent.builder()
                    .incidentId(incident.getId())
                    .incidentCode(incident.getIncidentCode())
                    .title(incident.getTitle())
                    .severity(incident.getSeverity() != null ? incident.getSeverity().name() : null)
                    .zoneId(incident.getZoneId())
                    .reportedByUserId(incident.getReportedByUserId())
                    .createdAt(incident.getCreatedAt())
                    .build();
            eventPublisher.publishIncidentCreated(event);
        } catch (Exception e) {
            log.warn("Could not publish incident created event: {}", e.getMessage());
        }
    }
    
    private void publishUrgentIncidentAlert(Incident incident) {
        try {
            UrgentIncidentAlertEvent event = UrgentIncidentAlertEvent.builder()
                    .incidentId(incident.getId())
                    .incidentCode(incident.getIncidentCode())
                    .title(incident.getTitle())
                    .severity(incident.getSeverity().name())
                    .zoneId(incident.getZoneId())
                    .affectedBoxesCount(incident.getAffectedBoxesCount())
                    .alertTime(Instant.now())
                    .build();
            eventPublisher.publishUrgentIncidentAlert(event);
            log.warn("ALERT: CRITICAL incident created: {}", incident.getIncidentCode());
        } catch (Exception e) {
            log.warn("Could not publish urgent incident alert: {}", e.getMessage());
        }
    }
}
