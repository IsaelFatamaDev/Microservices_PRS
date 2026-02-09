package pe.edu.vallegrande.vgmsorganizations.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsorganizations.application.mappers.ZoneMapper;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Zone;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.zone.IZoneRepository;
import pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.repositories.ZoneMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ZoneRepositoryImpl implements IZoneRepository {

    private final ZoneMongoRepository mongoRepository;
    private final ZoneMapper mapper;

    @Override
    public Mono<Zone> save(Zone zone) {
        return mongoRepository.save(mapper.toDocument(zone)).map(mapper::toModel);
    }

    @Override
    public Mono<Zone> update(Zone zone) {
        return mongoRepository.save(mapper.toDocument(zone)).map(mapper::toModel);
    }

    @Override
    public Mono<Zone> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toModel);
    }

    @Override
    public Flux<Zone> findAll() {
        return mongoRepository.findAll().map(mapper::toModel);
    }

    @Override
    public Flux<Zone> findByRecordStatus(RecordStatus status) {
        return mongoRepository.findByRecordStatus(status.name()).map(mapper::toModel);
    }

    @Override
    public Flux<Zone> findByOrganizationId(String organizationId) {
        return mongoRepository.findByOrganizationId(organizationId).map(mapper::toModel);
    }

    @Override
    public Flux<Zone> findByOrganizationIdAndRecordStatus(String organizationId, RecordStatus status) {
        return mongoRepository.findByOrganizationIdAndRecordStatus(organizationId, status.name()).map(mapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByZoneNameAndOrganizationId(String zoneName, String organizationId) {
        return mongoRepository.existsByZoneNameAndOrganizationId(zoneName, organizationId);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepository.deleteById(id);
    }
}