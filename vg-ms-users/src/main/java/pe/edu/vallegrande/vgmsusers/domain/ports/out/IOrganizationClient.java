package pe.edu.vallegrande.vgmsusers.domain.ports.out;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IOrganizationClient {

    Mono<Boolean> existsOrganization(String organizationId);

    Mono<Boolean> existsZone(String organizationId, String zoneId);

    Mono<Boolean> existsStreet(String zoneId, String streetId);

    Mono<Boolean> validateHierarchy(String organizationId, String zoneId, String streetId);
}


