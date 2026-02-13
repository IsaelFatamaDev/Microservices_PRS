package pe.edu.vallegrande.vgmsclaims.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentResolution;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.MaterialUsed;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.ResolutionType;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IIncidentResolutionRepository;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents.IncidentResolutionDocument;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.repositories.IncidentResolutionMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Incident resolution repository port implementation using MongoDB
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IncidentResolutionRepositoryImpl implements IIncidentResolutionRepository {

    private final IncidentResolutionMongoRepository resolutionRepository;

    @Override
    public Mono<IncidentResolution> save(IncidentResolution resolution) {
        log.debug("Saving resolution de incident: {}", resolution.getId());
        IncidentResolutionDocument document = toDocument(resolution);
        document.prePersist();
        return resolutionRepository.save(document)
                .map(this::toDomain);
    }

    @Override
    public Mono<IncidentResolution> findById(String id) {
        log.debug("Finding resolution by ID: {}", id);
        return resolutionRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Mono<IncidentResolution> findByIncidentId(String incidentId) {
        log.debug("Finding resolution by incident: {}", incidentId);
        return resolutionRepository.findByIncidentId(incidentId)
                .map(this::toDomain);
    }

    @Override
    public Flux<IncidentResolution> findByTechnicianId(String technicianId) {
        log.debug("Finding resoluciones by technician: {}", technicianId);
        return resolutionRepository.findAll()
                .filter(doc -> technicianId.equals(doc.getResolvedByUserId()))
                .map(this::toDomain);
    }

    @Override
    public Flux<IncidentResolution> findByResolutionType(String resolutionType) {
        log.debug("Finding resoluciones by type: {}", resolutionType);
        return resolutionRepository.findAll()
                .filter(doc -> resolutionType.equals(doc.getResolutionType()))
                .map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByIncidentId(String incidentId) {
        log.debug("Checking existencia de resolution para incident: {}", incidentId);
        return resolutionRepository.findByIncidentId(incidentId)
                .hasElement();
    }

    @Override
    public Mono<Void> deleteByIncidentId(String incidentId) {
        log.debug("Deleting resolution of the incident: {}", incidentId);
        return resolutionRepository.findByIncidentId(incidentId)
                .flatMap(doc -> resolutionRepository.deleteById(doc.getId()))
                .then();
    }

    // ========== Mapping methods ==========

    private IncidentResolution toDomain(IncidentResolutionDocument document) {
        return IncidentResolution.builder()
                .id(document.getId())
                .incidentId(document.getIncidentId())
                .resolutionDate(document.getResolutionDate())
                .resolutionType(parseResolutionType(document.getResolutionType()))
                .actionsTaken(document.getActionsTaken())
                .materialsUsed(toMaterialsDomain(document.getMaterialsUsed()))
                .laborHours(document.getLaborHours())
                .totalCost(document.getTotalCost())
                .resolvedByUserId(document.getResolvedByUserId())
                .qualityCheck(document.getQualityCheck())
                .followUpRequired(document.getFollowUpRequired())
                .resolutionNotes(document.getResolutionNotes())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    private IncidentResolutionDocument toDocument(IncidentResolution domain) {
        return IncidentResolutionDocument.builder()
                .id(domain.getId())
                .incidentId(domain.getIncidentId())
                .resolutionDate(domain.getResolutionDate())
                .resolutionType(domain.getResolutionType() != null ? domain.getResolutionType().name() : null)
                .actionsTaken(domain.getActionsTaken())
                .materialsUsed(toMaterialsDocument(domain.getMaterialsUsed()))
                .laborHours(domain.getLaborHours())
                .totalCost(domain.getTotalCost())
                .resolvedByUserId(domain.getResolvedByUserId())
                .qualityCheck(domain.getQualityCheck())
                .followUpRequired(domain.getFollowUpRequired())
                .resolutionNotes(domain.getResolutionNotes())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private List<MaterialUsed> toMaterialsDomain(List<IncidentResolutionDocument.MaterialUsedEmbedded> documents) {
        if (documents == null || documents.isEmpty()) {
            return new ArrayList<>();
        }
        return documents.stream()
                .map(doc -> new MaterialUsed(
                        doc.getProductId(),
                        doc.getQuantity(),
                        doc.getUnit(),
                        doc.getUnitCost()
                ))
                .collect(Collectors.toList());
    }

    private List<IncidentResolutionDocument.MaterialUsedEmbedded> toMaterialsDocument(List<MaterialUsed> materials) {
        if (materials == null || materials.isEmpty()) {
            return new ArrayList<>();
        }
        return materials.stream()
                .map(mat -> IncidentResolutionDocument.MaterialUsedEmbedded.builder()
                        .productId(mat.getProductId())
                        .quantity(mat.getQuantity())
                        .unit(mat.getUnit())
                        .unitCost(mat.getUnitCost())
                        .build())
                .collect(Collectors.toList());
    }

    private ResolutionType parseResolutionType(String type) {
        if (type == null) return null;
        try {
            return ResolutionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
