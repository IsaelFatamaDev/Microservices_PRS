package pe.edu.vallegrande.vgmsusers.infrastructure.persistence.repositories;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsusers.infrastructure.persistence.entities.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserR2dbcRepository extends R2dbcRepository<UserEntity, String> {

    Mono<UserEntity> findByDocumentNumber(String documentNumber);

    Mono<UserEntity> findByEmail(String email);

    Mono<Boolean> existsByDocumentNumber(String documentNumber);

    Flux<UserEntity> findByRecordStatus(String recordStatus);

    Flux<UserEntity> findByOrganizationId(String organizationId);

    Flux<UserEntity> findByOrganizationIdAndRecordStatus(String organizationId, String recordStatus);

    Flux<UserEntity> findByRole(String role);
}
