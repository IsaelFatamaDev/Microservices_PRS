package pe.edu.vallegrande.vgmsorganizations.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsorganizations.application.mappers.OrganizationMapper;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Organization;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization.IOrganizationRepository;
import pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.repositories.OrganizationMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
@SuppressWarnings("null")
public class OrganizationRepositoryImpl implements IOrganizationRepository {

    private final OrganizationMongoRepository mongoRepository;
    private final OrganizationMapper mapper;

    @Override
    public Mono<Organization> save(Organization organization) {
        return mongoRepository.save(mapper.toDocument(organization))
                .map(mapper::toModel);
    }

    @Override
    public Mono<Organization> update(Organization organization) {
        return mongoRepository.save(mapper.toDocument(organization))
                .map(mapper::toModel);
    }

    @Override
    public Mono<Organization> findById(String id) {
        return mongoRepository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public Flux<Organization> findAll() {
        return mongoRepository.findAll()
                .map(mapper::toModel);
    }

    @Override
    public Flux<Organization> findByRecordStatus(RecordStatus status) {
        return mongoRepository.findByRecordStatus(status.name())
                .map(mapper::toModel);
    }

    @Override
    public Flux<Organization> findByRecordStatus(String status) {
        return mongoRepository.findByRecordStatus(status)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Organization> findByOrganizationName(String name) {
        return mongoRepository.findByOrganizationName(name)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByOrganizationName(String name) {
        return mongoRepository.existsByOrganizationName(name);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepository.deleteById(id);
    }
}
