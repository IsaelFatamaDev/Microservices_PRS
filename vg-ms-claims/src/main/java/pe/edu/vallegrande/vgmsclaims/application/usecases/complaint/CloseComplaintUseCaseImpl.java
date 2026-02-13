package pe.edu.vallegrande.vgmsclaims.application.usecases.complaint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific.ComplaintAlreadyClosedException;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific.ComplaintNotFoundException;
import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.ComplaintStatus;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaint.ICloseComplaintUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IClaimsEventPublisher;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IComplaintRepository;
import pe.edu.vallegrande.vgmsclaims.application.events.complaint.ComplaintClosedEvent;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Use case implementation for closing complaints
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CloseComplaintUseCaseImpl implements ICloseComplaintUseCase {
    
    private final IComplaintRepository complaintRepository;
    private final IClaimsEventPublisher eventPublisher;
    
    @Override
    public Mono<Complaint> execute(String id, Integer satisfactionRating) {
        log.info("Closing complaint with ID: {}", id);
        
        return complaintRepository.findById(id)
                .switchIfEmpty(Mono.error(new ComplaintNotFoundException(id)))
                .flatMap(complaint -> {
                    if (complaint.getStatus() == ComplaintStatus.CLOSED) {
                        return Mono.error(new ComplaintAlreadyClosedException(id));
                    }
                    
                    complaint.close(satisfactionRating);
                    return complaintRepository.save(complaint);
                })
                .doOnSuccess(saved -> {
                    log.info("Complaint closed: {}", saved.getComplaintCode());
                    publishComplaintClosedEvent(saved);
                })
                .doOnError(error -> log.error("Error closing complaint: {}", error.getMessage()));
    }
    
    private void publishComplaintClosedEvent(Complaint complaint) {
        try {
            ComplaintClosedEvent event = ComplaintClosedEvent.builder()
                    .complaintId(complaint.getId())
                    .complaintCode(complaint.getComplaintCode())
                    .satisfactionRating(complaint.getSatisfactionRating())
                    .closedAt(Instant.now())
                    .build();
            eventPublisher.publishComplaintClosed(event);
        } catch (Exception e) {
            log.warn("Could not publish complaint closed event: {}", e.getMessage());
        }
    }
}
