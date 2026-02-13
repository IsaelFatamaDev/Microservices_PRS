package pe.edu.vallegrande.vgmsclaims.application.usecases.complaint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific.ComplaintNotFoundException;
import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaint.IDeleteComplaintUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IComplaintRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Use case implementation for soft deleting complaints
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteComplaintUseCaseImpl implements IDeleteComplaintUseCase {
    
    private final IComplaintRepository complaintRepository;
    
    @Override
    public Mono<Complaint> execute(String id) {
        log.info("Deleting complaint with ID: {}", id);
        
        return complaintRepository.findById(id)
                .switchIfEmpty(Mono.error(new ComplaintNotFoundException(id)))
                .flatMap(complaint -> {
                    complaint.setRecordStatus(RecordStatus.INACTIVE);
                    complaint.setUpdatedAt(Instant.now());
                    return complaintRepository.save(complaint);
                })
                .doOnSuccess(deleted -> log.info("Complaint deleted (soft delete): {}", deleted.getComplaintCode()))
                .doOnError(error -> log.error("Error deleting complaint: {}", error.getMessage()));
    }
}
