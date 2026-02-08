package pe.edu.vallegrande.vgmsusers.domain.ports.in;

import pe.edu.vallegrande.vgmsusers.domain.models.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetUserUseCase {

    Mono<User> findById(String id);

    Mono<User> findByDocumentNumber(String documentNumber);

    Mono<User> findByEmail(String email);

    Flux<User> findAllActive();

    Flux<User> findAll();

    Flux<User> findByOrganizationId(String organizationId);

    Flux<User> findActiveByOrganizationId(String organizationId);

    Mono<Boolean> existsByDocumentNumber(String documentNumber);
}
