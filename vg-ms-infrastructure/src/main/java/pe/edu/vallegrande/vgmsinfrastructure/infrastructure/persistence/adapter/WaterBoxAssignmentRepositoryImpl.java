package pe.edu.vallegrande.vgmsinfrastructure.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBoxAssignment;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.AssignmentStatus;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IWaterBoxAssignmentRepository;
import pe.edu.vallegrande.vgmsinfrastructure.infrastructure.persistence.entity.WaterBoxAssignmentEntity;
import pe.edu.vallegrande.vgmsinfrastructure.infrastructure.persistence.repository.WaterBoxAssignmentR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WaterBoxAssignmentRepositoryImpl implements IWaterBoxAssignmentRepository {

    private final WaterBoxAssignmentR2dbcRepository r2dbcRepository;

    @Override
    public Mono<WaterBoxAssignment> save(WaterBoxAssignment assignment) {
        WaterBoxAssignmentEntity entity = toEntity(assignment);
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        return r2dbcRepository.save(entity).map(this::toDomain);
    }

    @Override
    public Mono<WaterBoxAssignment> findById(String id) {
        return r2dbcRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Flux<WaterBoxAssignment> findAll() {
        return r2dbcRepository.findAll().map(this::toDomain);
    }

    @Override
    public Flux<WaterBoxAssignment> findByWaterBoxId(String waterBoxId) {
        return r2dbcRepository.findByWaterBoxId(waterBoxId).map(this::toDomain);
    }

    @Override
    public Flux<WaterBoxAssignment> findByUserId(String userId) {
        return r2dbcRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public Mono<WaterBoxAssignment> findActiveByWaterBoxId(String waterBoxId) {
        return r2dbcRepository.findActiveByWaterBoxId(waterBoxId).map(this::toDomain);
    }

    private WaterBoxAssignmentEntity toEntity(WaterBoxAssignment domain) {
        return WaterBoxAssignmentEntity.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .waterBoxId(domain.getWaterBoxId())
                .userId(domain.getUserId())
                .assignmentDate(domain.getAssignmentDate())
                .assignmentStatus(domain.getAssignmentStatus() != null ? domain.getAssignmentStatus().name() : AssignmentStatus.ACTIVE.name())
                .endDate(domain.getEndDate())
                .recordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : "ACTIVE")
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }

    private WaterBoxAssignment toDomain(WaterBoxAssignmentEntity entity) {
        return WaterBoxAssignment.builder()
                .id(entity.getId())
                .organizationId(entity.getOrganizationId())
                .waterBoxId(entity.getWaterBoxId())
                .userId(entity.getUserId())
                .assignmentDate(entity.getAssignmentDate())
                .assignmentStatus(entity.getAssignmentStatus() != null ? AssignmentStatus.valueOf(entity.getAssignmentStatus()) : AssignmentStatus.ACTIVE)
                .endDate(entity.getEndDate())
                .recordStatus(entity.getRecordStatus() != null ? RecordStatus.valueOf(entity.getRecordStatus()) : RecordStatus.ACTIVE)
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
