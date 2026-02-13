package pe.edu.vallegrande.vgmsclaims.application.usecases.complaint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.application.events.complaint.ComplaintResponseAddedEvent;
import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintResponse;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaint.IAddResponseUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IClaimsEventPublisher;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IComplaintRepository;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IComplaintResponseRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

/**
 * Use case implementation for adding responses to complaints
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddResponseUseCaseImpl implements IAddResponseUseCase {

    private final IComplaintRepository complaintRepository;
    private final IComplaintResponseRepository complaintResponseRepository;
    private final IClaimsEventPublisher eventPublisher;

    @Override
    public Mono<Complaint> execute(String complaintId, ComplaintResponse response) {
        log.info("Adding response to complaint: {}", complaintId);

        return complaintRepository.findById(complaintId)
                .switchIfEmpty(Mono.error(new RuntimeException("Complaint not found: " + complaintId)))
                .flatMap(complaint -> {
                    response.setId(UUID.randomUUID().toString());
                    response.setComplaintId(complaintId);
                    response.setResponseDate(Instant.now());
                    response.setCreatedAt(Instant.now());

                    return complaintResponseRepository.save(response)
                            .then(Mono.defer(() -> {
                                complaint.setUpdatedAt(Instant.now());
                                return complaintRepository.save(complaint);
                            }));
                })
                .doOnSuccess(saved -> {
                    log.info("Response added successfully to complaint: {}", complaintId);
                    publishResponseAddedEvent(response);
                })
                .doOnError(error -> log.error("Error adding response to complaint {}: {}", complaintId,
                        error.getMessage()));
    }

    private void publishResponseAddedEvent(ComplaintResponse response) {
        try {
            ComplaintResponseAddedEvent event = ComplaintResponseAddedEvent.builder()
                    .responseId(response.getId())
                    .complaintId(response.getComplaintId())
                    .responseType(response.getResponseType() != null ? response.getResponseType().name() : null)
                    .respondedByUserId(response.getRespondedByUserId())
                    .createdAt(response.getCreatedAt())
                    .build();
            eventPublisher.publishComplaintResponseAdded(event);
        } catch (Exception e) {
            log.warn("Could not publish response added event: {}", e.getMessage());
        }
    }
}
