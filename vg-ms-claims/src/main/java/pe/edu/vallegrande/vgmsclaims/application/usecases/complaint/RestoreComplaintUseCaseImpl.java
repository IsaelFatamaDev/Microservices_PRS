package pe.edu.vallegrande.vgmsclaims.application.usecases.complaint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific.ComplaintNotFoundException;
import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaint.IRestoreComplaintUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IComplaintRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Use case implementation for restoring deleted complaints
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreComplaintUseCaseImpl implements IRestoreComplaintUseCase {
    
    private final IComplaintRepository complaintRepository;
    
    @Override
    public Mono<Complaint> execute(String id) {
        log.info("Restoring complaint with ID: {}", id);
        
        return complaintRepository.findById(id)
                .switchIfEmpty(Mono.error(new ComplaintNotFoundException(id)))
                .flatMap(complaint -> {
                    complaint.setRecordStatus(RecordStatus.ACTIVE);
                    complaint.setUpdatedAt(Instant.now());
                    return complaintRepository.save(complaint);
                })
                .doOnSuccess(restored -> log.info("Complaint restored: {}", restored.getComplaintCode()))
                .doOnError(error -> log.error("Error restoring complaint: {}", error.getMessage()));
    }
}
