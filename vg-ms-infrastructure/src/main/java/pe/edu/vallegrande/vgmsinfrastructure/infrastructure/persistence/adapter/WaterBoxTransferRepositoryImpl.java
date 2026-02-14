package pe.edu.vallegrande.vgmsinfrastructure.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBoxTransfer;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IWaterBoxTransferRepository;
import pe.edu.vallegrande.vgmsinfrastructure.infrastructure.persistence.entity.WaterBoxTransferEntity;
import pe.edu.vallegrande.vgmsinfrastructure.infrastructure.persistence.repository.WaterBoxTransferR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WaterBoxTransferRepositoryImpl implements IWaterBoxTransferRepository {

    private final WaterBoxTransferR2dbcRepository r2dbcRepository;

    @Override
    public Mono<WaterBoxTransfer> save(WaterBoxTransfer transfer) {
        WaterBoxTransferEntity entity = toEntity(transfer);
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
            entity.setNewEntity(true);
        }
        return r2dbcRepository.save(entity).map(this::toDomain);
    }

    @Override
    public Mono<WaterBoxTransfer> findById(String id) {
        return r2dbcRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Flux<WaterBoxTransfer> findAll() {
        return r2dbcRepository.findAll().map(this::toDomain);
    }

    @Override
    public Flux<WaterBoxTransfer> findByWaterBoxId(String waterBoxId) {
        return r2dbcRepository.findByWaterBoxId(waterBoxId).map(this::toDomain);
    }

    private WaterBoxTransferEntity toEntity(WaterBoxTransfer domain) {
        return WaterBoxTransferEntity.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .waterBoxId(domain.getWaterBoxId())
                .fromUserId(domain.getFromUserId())
                .toUserId(domain.getToUserId())
                .transferDate(domain.getTransferDate())
                .transferFee(domain.getTransferFee())
                .notes(domain.getNotes())
                .recordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus() : "ACTIVE")
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .build();
    }

    private WaterBoxTransfer toDomain(WaterBoxTransferEntity entity) {
        return WaterBoxTransfer.builder()
                .id(entity.getId())
                .organizationId(entity.getOrganizationId())
                .waterBoxId(entity.getWaterBoxId())
                .fromUserId(entity.getFromUserId())
                .toUserId(entity.getToUserId())
                .transferDate(entity.getTransferDate())
                .transferFee(entity.getTransferFee())
                .notes(entity.getNotes())
                .recordStatus(entity.getRecordStatus())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}
