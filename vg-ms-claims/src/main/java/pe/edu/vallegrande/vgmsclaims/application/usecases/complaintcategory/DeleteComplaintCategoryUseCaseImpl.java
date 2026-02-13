package pe.edu.vallegrande.vgmsclaims.application.usecases.complaintcategory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.base.NotFoundException;
import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintCategory;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaintcategory.IDeleteComplaintCategoryUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IComplaintCategoryRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Use case implementation for soft deleting complaint categories
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteComplaintCategoryUseCaseImpl implements IDeleteComplaintCategoryUseCase {
    
    private final IComplaintCategoryRepository categoryRepository;
    
    @Override
    public Mono<ComplaintCategory> execute(String id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("ComplaintCategory", id)))
                .flatMap(existing -> {
                    existing.setRecordStatus(RecordStatus.INACTIVE);
                    existing.setUpdatedAt(Instant.now());
                    
                    log.info("Soft deleting complaint category: {}", id);
                    return categoryRepository.save(existing);
                })
                .doOnSuccess(deleted -> log.info("Complaint category soft deleted: {}", deleted.getId()));
    }
}
