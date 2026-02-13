package pe.edu.vallegrande.vgmsclaims.application.usecases.complaintcategory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.base.NotFoundException;
import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintCategory;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaintcategory.IUpdateComplaintCategoryUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IComplaintCategoryRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Use case implementation for updating complaint categories
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateComplaintCategoryUseCaseImpl implements IUpdateComplaintCategoryUseCase {
    
    private final IComplaintCategoryRepository categoryRepository;
    
    @Override
    public Mono<ComplaintCategory> execute(String id, ComplaintCategory category) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("ComplaintCategory", id)))
                .flatMap(existing -> {
                    if (category.getCategoryName() != null) {
                        existing.setCategoryName(category.getCategoryName());
                    }
                    if (category.getDescription() != null) {
                        existing.setDescription(category.getDescription());
                    }
                    if (category.getPriorityLevel() != null) {
                        existing.setPriorityLevel(category.getPriorityLevel());
                    }
                    if (category.getMaxResponseTime() != null) {
                        existing.setMaxResponseTime(category.getMaxResponseTime());
                    }
                    existing.setUpdatedAt(Instant.now());
                    
                    log.info("Updating complaint category: {}", id);
                    return categoryRepository.save(existing);
                })
                .doOnSuccess(updated -> log.info("Complaint category updated: {}", updated.getId()));
    }
}
