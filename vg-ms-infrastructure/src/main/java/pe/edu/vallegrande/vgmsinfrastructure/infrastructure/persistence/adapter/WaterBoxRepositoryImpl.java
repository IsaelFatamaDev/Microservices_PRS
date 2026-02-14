package pe.edu.vallegrande.vgmsinfrastructure.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBox;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.BoxType;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IWaterBoxRepository;
import pe.edu.vallegrande.vgmsinfrastructure.infrastructure.persistence.entity.WaterBoxEntity;
import pe.edu.vallegrande.vgmsinfrastructure.infrastructure.persistence.repository.WaterBoxR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WaterBoxRepositoryImpl implements IWaterBoxRepository {

    private final WaterBoxR2dbcRepository r2dbcRepository;

    @Override
    public Mono<WaterBox> save(WaterBox waterBox) {
        WaterBoxEntity entity = toEntity(waterBox);
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
            entity.setNewEntity(true);
        }
        return r2dbcRepository.save(entity).map(this::toDomain);
    }

    @Override
    public Mono<WaterBox> findById(String id) {
        return r2dbcRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Flux<WaterBox> findAll() {
        return r2dbcRepository.findAll().map(this::toDomain);
    }

    @Override
    public Flux<WaterBox> findByRecordStatus(RecordStatus status) {
        return r2dbcRepository.findByRecordStatus(status.name()).map(this::toDomain);
    }

    @Override
    public Flux<WaterBox> findByZoneId(String zoneId) {
        return r2dbcRepository.findByZoneId(zoneId).map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByBoxCode(String boxCode) {
        return r2dbcRepository.existsByBoxCode(boxCode);
    }

    private WaterBoxEntity toEntity(WaterBox domain) {
        return WaterBoxEntity.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .boxCode(domain.getBoxCode())
                .boxType(domain.getBoxType() != null ? domain.getBoxType().name() : null)
                .installationDate(domain.getInstallationDate())
                .zoneId(domain.getZoneId())
                .streetId(domain.getStreetId())
                .address(domain.getAddress())
                .currentAssignmentId(domain.getCurrentAssignmentId())
                .isActive(domain.getIsActive())
                .recordStatus(
                        domain.getRecordStatus() != null ? domain.getRecordStatus().name() : RecordStatus.ACTIVE.name())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }

    private WaterBox toDomain(WaterBoxEntity entity) {
        return WaterBox.builder()
                .id(entity.getId())
                .organizationId(entity.getOrganizationId())
                .boxCode(entity.getBoxCode())
                .boxType(entity.getBoxType() != null ? BoxType.valueOf(entity.getBoxType()) : null)
                .installationDate(entity.getInstallationDate())
                .zoneId(entity.getZoneId())
                .streetId(entity.getStreetId())
                .address(entity.getAddress())
                .currentAssignmentId(entity.getCurrentAssignmentId())
                .isActive(entity.getIsActive())
                .recordStatus(entity.getRecordStatus() != null ? RecordStatus.valueOf(entity.getRecordStatus())
                        : RecordStatus.ACTIVE)
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
