package pe.edu.vallegrande.vgmsclaims.application.usecases.complaintcategory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintCategory;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaintcategory.IGetComplaintCategoryUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IComplaintCategoryRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Use case implementation for getting complaint categories
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetComplaintCategoryUseCaseImpl implements IGetComplaintCategoryUseCase {
    
    private final IComplaintCategoryRepository categoryRepository;
    
    @Override
    public Flux<ComplaintCategory> findAll() {
        return categoryRepository.findAll();
    }
    
    @Override
    public Mono<ComplaintCategory> findById(String id) {
        return categoryRepository.findById(id);
    }
    
    @Override
    public Flux<ComplaintCategory> findByOrganizationId(String organizationId) {
        return categoryRepository.findByOrganizationId(organizationId);
    }
    
    @Override
    public Flux<ComplaintCategory> findActiveByOrganizationId(String organizationId) {
        return categoryRepository.findActiveByOrganizationId(organizationId);
    }
}
