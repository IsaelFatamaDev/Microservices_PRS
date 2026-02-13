package pe.edu.vallegrande.vgmsclaims.application.usecases.complaintcategory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.base.NotFoundException;
import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintCategory;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaintcategory.IRestoreComplaintCategoryUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IComplaintCategoryRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Use case implementation for restoring complaint categories
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreComplaintCategoryUseCaseImpl implements IRestoreComplaintCategoryUseCase {
    
    private final IComplaintCategoryRepository categoryRepository;
    
    @Override
    public Mono<ComplaintCategory> execute(String id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("ComplaintCategory", id)))
                .flatMap(existing -> {
                    existing.setRecordStatus(RecordStatus.ACTIVE);
                    existing.setUpdatedAt(Instant.now());
                    
                    log.info("Restoring complaint category: {}", id);
                    return categoryRepository.save(existing);
                })
                .doOnSuccess(restored -> log.info("Complaint category restored: {}", restored.getId()));
    }
}
