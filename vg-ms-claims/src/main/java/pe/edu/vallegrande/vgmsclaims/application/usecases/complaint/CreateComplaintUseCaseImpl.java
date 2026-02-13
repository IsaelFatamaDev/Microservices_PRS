package pe.edu.vallegrande.vgmsclaims.application.usecases.complaint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.ComplaintStatus;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaint.ICreateComplaintUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IClaimsEventPublisher;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IComplaintRepository;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.ISecurityContext;
import pe.edu.vallegrande.vgmsclaims.application.events.complaint.ComplaintCreatedEvent;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

/**
 * Use case implementation for creating complaints
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateComplaintUseCaseImpl implements ICreateComplaintUseCase {
    
    private final IComplaintRepository complaintRepository;
    private final IClaimsEventPublisher eventPublisher;
    private final ISecurityContext securityContext;
    
    @Override
    public Mono<Complaint> execute(Complaint complaint) {
        log.info("Creando new complaint: {}", complaint.getSubject());
        
        return securityContext.getCurrentUserId()
                .flatMap(userId -> {
                    // Set default values
                    complaint.setComplaintCode(generateComplaintCode());
                    complaint.setUserId(userId);
                    complaint.setStatus(ComplaintStatus.RECEIVED);
                    complaint.setRecordStatus(RecordStatus.ACTIVE);
                    complaint.setCreatedAt(Instant.now());
                    complaint.setComplaintDate(Instant.now());
                    
                    return complaintRepository.save(complaint)
                            .doOnSuccess(saved -> {
                                log.info("Complaint created successfully: {}", saved.getComplaintCode());
                                publishComplaintCreatedEvent(saved);
                            })
                            .doOnError(error -> log.error("Error creating complaint: {}", error.getMessage()));
                });
    }
    
    private String generateComplaintCode() {
        return "QUE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private void publishComplaintCreatedEvent(Complaint complaint) {
        try {
            ComplaintCreatedEvent event = ComplaintCreatedEvent.builder()
                    .complaintId(complaint.getId())
                    .complaintCode(complaint.getComplaintCode())
                    .userId(complaint.getUserId())
                    .subject(complaint.getSubject())
                    .priority(complaint.getPriority() != null ? complaint.getPriority().name() : null)
                    .createdAt(complaint.getCreatedAt())
                    .build();
            eventPublisher.publishComplaintCreated(event);
        } catch (Exception e) {
            log.warn("Could not publish complaint created event: {}", e.getMessage());
        }
    }
}
