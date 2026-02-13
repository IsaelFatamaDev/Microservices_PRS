package pe.edu.vallegrande.vgmsclaims.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintCategory;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IComplaintCategoryRepository;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents.ComplaintCategoryDocument;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.repositories.ComplaintCategoryMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Complaint category repository port implementation using MongoDB
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ComplaintCategoryRepositoryImpl implements IComplaintCategoryRepository {

    private final ComplaintCategoryMongoRepository categoryRepository;

    @Override
    public Mono<ComplaintCategory> save(ComplaintCategory category) {
        log.debug("Saving category de complaint: {}", category.getId());
        ComplaintCategoryDocument document = toDocument(category);
        document.prePersist();
        return categoryRepository.save(document)
                .map(this::toDomain);
    }

    @Override
    public Mono<ComplaintCategory> findById(String id) {
        log.debug("Finding category by ID: {}", id);
        return categoryRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Flux<ComplaintCategory> findAll() {
        log.debug("Finding all the categorys");
        return categoryRepository.findAll()
                .map(this::toDomain);
    }

    @Override
    public Flux<ComplaintCategory> findByOrganizationId(String organizationId) {
        log.debug("Finding categorys by organization: {}", organizationId);
        return categoryRepository.findByOrganizationId(organizationId)
                .map(this::toDomain);
    }

    @Override
    public Flux<ComplaintCategory> findActiveByOrganizationId(String organizationId) {
        log.debug("Finding categorys active by organization: {}", organizationId);
        return categoryRepository.findByOrganizationId(organizationId)
                .filter(doc -> RecordStatus.ACTIVE.name().equals(doc.getRecordStatus()))
                .map(this::toDomain);
    }

    @Override
    public Mono<ComplaintCategory> findByCategoryCodeAndOrganizationId(String categoryCode, String organizationId) {
        log.debug("Finding category by code {} and organization {}", categoryCode, organizationId);
        return categoryRepository.findByOrganizationId(organizationId)
                .filter(doc -> categoryCode.equals(doc.getCategoryCode()))
                .next()
                .map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByCategoryCodeAndOrganizationId(String categoryCode, String organizationId) {
        log.debug("Checking existencia de category with code {} en organization {}", categoryCode, organizationId);
        return categoryRepository.findByOrganizationId(organizationId)
                .filter(doc -> categoryCode.equals(doc.getCategoryCode()))
                .hasElements();
    }

    // ========== Mapping methods ==========

    private ComplaintCategory toDomain(ComplaintCategoryDocument document) {
        return ComplaintCategory.builder()
                .id(document.getId())
                .organizationId(document.getOrganizationId())
                .categoryCode(document.getCategoryCode())
                .categoryName(document.getCategoryName())
                .description(document.getDescription())
                .priorityLevel(document.getPriorityLevel())
                .maxResponseTime(document.getMaxResponseTime())
                .recordStatus(parseRecordStatus(document.getRecordStatus()))
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    private ComplaintCategoryDocument toDocument(ComplaintCategory domain) {
        return ComplaintCategoryDocument.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .categoryCode(domain.getCategoryCode())
                .categoryName(domain.getCategoryName())
                .description(domain.getDescription())
                .priorityLevel(domain.getPriorityLevel())
                .maxResponseTime(domain.getMaxResponseTime())
                .recordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : null)
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private RecordStatus parseRecordStatus(String status) {
        if (status == null) return null;
        try {
            return RecordStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
