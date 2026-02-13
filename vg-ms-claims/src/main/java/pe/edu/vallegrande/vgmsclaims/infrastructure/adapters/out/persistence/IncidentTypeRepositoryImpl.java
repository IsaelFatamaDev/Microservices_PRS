package pe.edu.vallegrande.vgmsclaims.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentType;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IIncidentTypeRepository;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents.IncidentTypeDocument;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.repositories.IncidentTypeMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Incident type repository port implementation using MongoDB
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IncidentTypeRepositoryImpl implements IIncidentTypeRepository {

    private final IncidentTypeMongoRepository typeRepository;

    @Override
    public Mono<IncidentType> save(IncidentType incidentType) {
        log.debug("Saving incident type: {}", incidentType.getId());
        IncidentTypeDocument document = toDocument(incidentType);
        document.prePersist();
        return typeRepository.save(document)
                .map(this::toDomain);
    }

    @Override
    public Mono<IncidentType> findById(String id) {
        log.debug("Finding incident type by ID: {}", id);
        return typeRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Flux<IncidentType> findAll() {
        log.debug("Finding all the incident types");
        return typeRepository.findAll()
                .map(this::toDomain);
    }

    @Override
    public Flux<IncidentType> findByOrganizationId(String organizationId) {
        log.debug("Finding incident types by organization: {}", organizationId);
        return typeRepository.findByOrganizationId(organizationId)
                .map(this::toDomain);
    }

    @Override
    public Flux<IncidentType> findActiveByOrganizationId(String organizationId) {
        log.debug("Finding incident types active by organization: {}", organizationId);
        return typeRepository.findByOrganizationId(organizationId)
                .filter(doc -> RecordStatus.ACTIVE.name().equals(doc.getRecordStatus()))
                .map(this::toDomain);
    }

    @Override
    public Mono<IncidentType> findByTypeCodeAndOrganizationId(String typeCode, String organizationId) {
        log.debug("Finding incident type by code {} and organization {}", typeCode, organizationId);
        return typeRepository.findByOrganizationId(organizationId)
                .filter(doc -> typeCode.equals(doc.getTypeCode()))
                .next()
                .map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByTypeCodeAndOrganizationId(String typeCode, String organizationId) {
        log.debug("Checking existencia de type with code {} en organization {}", typeCode, organizationId);
        return typeRepository.findByOrganizationId(organizationId)
                .filter(doc -> typeCode.equals(doc.getTypeCode()))
                .hasElements();
    }

    // ========== Mapping methods ==========

    private IncidentType toDomain(IncidentTypeDocument document) {
        return IncidentType.builder()
                .id(document.getId())
                .organizationId(document.getOrganizationId())
                .typeCode(document.getTypeCode())
                .typeName(document.getTypeName())
                .description(document.getDescription())
                .priorityLevel(document.getPriorityLevel())
                .estimatedResolutionTime(document.getEstimatedResolutionTime())
                .requiresExternalService(document.getRequiresExternalService())
                .recordStatus(parseRecordStatus(document.getRecordStatus()))
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    private IncidentTypeDocument toDocument(IncidentType domain) {
        return IncidentTypeDocument.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .typeCode(domain.getTypeCode())
                .typeName(domain.getTypeName())
                .description(domain.getDescription())
                .priorityLevel(domain.getPriorityLevel())
                .estimatedResolutionTime(domain.getEstimatedResolutionTime())
                .requiresExternalService(domain.getRequiresExternalService())
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
