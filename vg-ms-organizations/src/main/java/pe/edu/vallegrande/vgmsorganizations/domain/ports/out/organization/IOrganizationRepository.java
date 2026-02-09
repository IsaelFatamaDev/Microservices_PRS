package pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Organization;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IOrganizationRepository {
    Mono<Organization> save(Organization organization);

    Mono<Organization> findById(String id);

    Mono<Organization> update(Organization organization);

    Mono<Organization> findByOrganizationName(String name);

    Flux<Organization> findAll();

    Flux<Organization> findByRecordStatus(String status);

    Flux<Organization> findByRecordStatus(RecordStatus status);

    Mono<Boolean> existsByOrganizationName(String name);

    Mono<Void> deleteById(String id);
}
