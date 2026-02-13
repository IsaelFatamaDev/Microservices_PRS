package pe.edu.vallegrande.vgmsclaims.application.usecases.complaint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific.ComplaintNotFoundException;
import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaint.IUpdateComplaintUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IClaimsEventPublisher;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IComplaintRepository;
import pe.edu.vallegrande.vgmsclaims.application.events.complaint.ComplaintUpdatedEvent;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Use case implementation for updating complaints
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateComplaintUseCaseImpl implements IUpdateComplaintUseCase {
    
    private final IComplaintRepository complaintRepository;
    private final IClaimsEventPublisher eventPublisher;
    
    @Override
    public Mono<Complaint> execute(String id, Complaint updatedData) {
        log.info("Updating complaint with ID: {}", id);
        
        return complaintRepository.findById(id)
                .switchIfEmpty(Mono.error(new ComplaintNotFoundException(id)))
                .flatMap(existing -> {
                    // Update allowed fields
                    if (updatedData.getSubject() != null) {
                        existing.setSubject(updatedData.getSubject());
                    }
                    if (updatedData.getDescription() != null) {
                        existing.setDescription(updatedData.getDescription());
                    }
                    if (updatedData.getPriority() != null) {
                        existing.setPriority(updatedData.getPriority());
                    }
                    if (updatedData.getStatus() != null) {
                        existing.setStatus(updatedData.getStatus());
                    }
                    if (updatedData.getAssignedToUserId() != null) {
                        existing.setAssignedToUserId(updatedData.getAssignedToUserId());
                    }
                    if (updatedData.getExpectedResolutionDate() != null) {
                        existing.setExpectedResolutionDate(updatedData.getExpectedResolutionDate());
                    }
                    
                    existing.setUpdatedAt(Instant.now());
                    
                    return complaintRepository.save(existing);
                })
                .doOnSuccess(saved -> {
                    log.info("Complaint updated: {}", saved.getComplaintCode());
                    publishComplaintUpdatedEvent(saved);
                })
                .doOnError(error -> log.error("Error updating complaint: {}", error.getMessage()));
    }
    
    private void publishComplaintUpdatedEvent(Complaint complaint) {
        try {
            ComplaintUpdatedEvent event = ComplaintUpdatedEvent.builder()
                    .complaintId(complaint.getId())
                    .complaintCode(complaint.getComplaintCode())
                    .status(complaint.getStatus() != null ? complaint.getStatus().name() : null)
                    .updatedAt(complaint.getUpdatedAt())
                    .build();
            eventPublisher.publishComplaintUpdated(event);
        } catch (Exception e) {
            log.warn("Could not publish complaint updated event: {}", e.getMessage());
        }
    }
}
