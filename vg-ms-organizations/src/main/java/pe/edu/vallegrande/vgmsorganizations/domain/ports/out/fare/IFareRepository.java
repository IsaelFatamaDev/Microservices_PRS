package pe.edu.vallegrande.vgmsorganizations.domain.ports.out.fare;

import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Fare;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface IFareRepository {
    Mono<Fare> save(Fare fare);

    Mono<Fare> update(Fare fare);

    Mono<Fare> findById(String id);

    Flux<Fare> findAll();

    Flux<Fare> findByRecordStatus(RecordStatus status);

    Flux<Fare> findByOrganizationId(String organizationId);

    Flux<Fare> findByOrganizationIdAndRecordStatus(String organizationId, RecordStatus status);

    Flux<Fare> findActiveByOrganizationAndType(String organizationId, String fareType);

    Mono<Void> deleteById(String id);
}
