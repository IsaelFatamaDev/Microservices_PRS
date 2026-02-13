package pe.edu.vallegrande.vgmsclaims.application.usecases.complaintcategory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintCategory;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaintcategory.ICreateComplaintCategoryUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IComplaintCategoryRepository;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.ISecurityContext;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Use case implementation for creating complaint categories
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateComplaintCategoryUseCaseImpl implements ICreateComplaintCategoryUseCase {
    
    private final IComplaintCategoryRepository categoryRepository;
    private final ISecurityContext securityContext;
    
    @Override
    public Mono<ComplaintCategory> execute(ComplaintCategory category) {
        return securityContext.getCurrentUser()
                .flatMap(user -> {
                    category.setOrganizationId(user.getOrganizationId());
                    category.setRecordStatus(RecordStatus.ACTIVE);
                    category.setCreatedAt(Instant.now());
                    category.setUpdatedAt(Instant.now());
                    
                    log.info("Creating complaint category: {} for organization: {}", 
                            category.getCategoryCode(), category.getOrganizationId());
                    
                    return categoryRepository.save(category);
                })
                .doOnSuccess(saved -> log.info("Complaint category created with id: {}", saved.getId()));
    }
}
