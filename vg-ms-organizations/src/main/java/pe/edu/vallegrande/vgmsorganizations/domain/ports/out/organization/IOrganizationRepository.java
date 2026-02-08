package pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization;

import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Organization;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface IOrganizationRepository {
    Mono<Organization> save(Organization organization);

    Mono<Organization> findById(String id);

    Mono<Organization> update(Organization organization);

    Mono<Organization> findByOrganizationName(String name);

    Flux<Organization> findAll();

    Flux<Organization> findByRecordStatus(String status);

    Mono<Boolean> existsByOrganizationName(String name);

    Mono<Void> deleteById(String id);
}
