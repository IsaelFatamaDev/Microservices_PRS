package pe.edu.vallegrande.vgmsusers.domain.ports.out;

import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsusers.domain.models.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface IUserRepository {

    Mono<User> save(User user);

    Mono<User> update(User user);

    Mono<User> findById(String id);

    Mono<User> findByDocumentNumber(String documentNumber);

    Mono<User> findByEmail(String email);

    Flux<User> findAll();

    Flux<User> findByOrganizationId(String organizationId);

    Flux<User> findByRecordStatus(String recordStatus);

    Flux<User> findByOrganizationIdAndRecordStatus(String organizationId, String recordStatus);

    Mono<Boolean> existsByDocumentNumber(String documentNumber);

    Mono<Void> deleteById(String id);

}
