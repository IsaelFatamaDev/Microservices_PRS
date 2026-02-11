package pe.edu.vallegrande.vgmsorganizations.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsorganizations.application.mappers.StreetMapper;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Street;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.street.IStreetRepository;
import pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.repositories.StreetMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("null")
public class StreetRepositoryImpl implements IStreetRepository {

    private final StreetMongoRepository mongoRepository;
    private final StreetMapper mapper;

    @Override
    public Mono<Street> save(Street street) {
        return mongoRepository.save(mapper.toDocument(street)).map(mapper::toModel);
    }

    @Override
    public Mono<Street> update(Street street) {
        return mongoRepository.save(mapper.toDocument(street)).map(mapper::toModel);
    }

    @Override
    public Mono<Street> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toModel);
    }

    @Override
    public Flux<Street> findAll() {
        return mongoRepository.findAll().map(mapper::toModel);
    }

    @Override
    public Flux<Street> findByRecordStatus(RecordStatus status) {
        return mongoRepository.findByRecordStatus(status.name()).map(mapper::toModel);
    }

    @Override
    public Flux<Street> findByZoneId(String zoneId) {
        return mongoRepository.findByZoneId(zoneId).map(mapper::toModel);
    }

    @Override
    public Flux<Street> findByZoneIdAndRecordStatus(String zoneId, RecordStatus status) {
        return mongoRepository.findByZoneIdAndRecordStatus(zoneId, status.name()).map(mapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByStreetNameAndZoneId(String streetName, String zoneId) {
        return mongoRepository.existsByStreetNameAndZoneId(streetName, zoneId);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepository.deleteById(id);
    }
}