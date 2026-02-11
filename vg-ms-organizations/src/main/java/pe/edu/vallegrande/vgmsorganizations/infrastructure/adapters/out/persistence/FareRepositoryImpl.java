package pe.edu.vallegrande.vgmsorganizations.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsorganizations.application.mappers.FareMapper;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Fare;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.fare.IFareRepository;
import pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.repositories.FareMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("null")
public class FareRepositoryImpl implements IFareRepository {

    private final FareMongoRepository mongoRepository;
    private final FareMapper mapper;

    @Override
    public Mono<Fare> save(Fare fare) {
        return mongoRepository.save(mapper.toDocument(fare)).map(mapper::toModel);
    }

    @Override
    public Mono<Fare> update(Fare fare) {
        return mongoRepository.save(mapper.toDocument(fare)).map(mapper::toModel);
    }

    @Override
    public Mono<Fare> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toModel);
    }

    @Override
    public Flux<Fare> findAll() {
        return mongoRepository.findAll().map(mapper::toModel);
    }

    @Override
    public Flux<Fare> findByRecordStatus(RecordStatus status) {
        return mongoRepository.findByRecordStatus(status.name()).map(mapper::toModel);
    }

    @Override
    public Flux<Fare> findByOrganizationId(String organizationId) {
        return mongoRepository.findByOrganizationId(organizationId).map(mapper::toModel);
    }

    @Override
    public Flux<Fare> findByOrganizationIdAndRecordStatus(String organizationId, RecordStatus status) {
        return mongoRepository.findByOrganizationIdAndRecordStatus(organizationId, status.name()).map(mapper::toModel);
    }

    @Override
    public Flux<Fare> findActiveByOrganizationAndType(String organizationId, String fareType) {
        return mongoRepository.findByOrganizationIdAndFareTypeAndRecordStatus(
                organizationId, fareType, RecordStatus.ACTIVE.name()).map(mapper::toModel);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepository.deleteById(id);
    }
}
