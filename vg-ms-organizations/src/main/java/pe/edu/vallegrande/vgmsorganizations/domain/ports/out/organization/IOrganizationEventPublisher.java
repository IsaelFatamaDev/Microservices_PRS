package pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Organization;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface IOrganizationEventPublisher {
    Mono<Void> publishOrganizationCreated(Organization organization, String createdBy);

    Mono<Void> publishOrganizationUpdated(Organization organization, Map<String, Object> changedFields, String updatedBy);

    Mono<Void> publishOrganizationDeleted(String organizationId, String deletedBy, String reason);

    Mono<Void> publishOrganizationRestored(String organizationId, String restoredBy);
}
