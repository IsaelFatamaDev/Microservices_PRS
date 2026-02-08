package pe.edu.vallegrande.vgmsorganizations.domain.ports.in.organization;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Organization;
import reactor.core.publisher.Mono;

public interface IUpdateOrganizationUseCase {
    Mono<Organization> execute(String id, Organization changes, String updatedBy);
}
