package pe.edu.vallegrande.vgmsusers.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsusers.application.mappers.UserMapper;
import pe.edu.vallegrande.vgmsusers.domain.models.User;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.IUserRepository;
import pe.edu.vallegrande.vgmsusers.infrastructure.persistence.repositories.UserR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
@SuppressWarnings("null")
public class UserRepositoryImpl implements IUserRepository {

    private final UserR2dbcRepository r2dbcRepository;
    private final UserMapper userMapper;

    @Override
    public Mono<User> save(User user) {
        log.debug("Saving user: {}", user.getDocumentNumber());
        return r2dbcRepository.save(userMapper.toEntity(user))
                .doOnNext(entity -> entity.setNotNew())
                .map(userMapper::toModel);
    }

    @Override
    public Mono<User> update(User user) {
        log.debug("Updating user: {}", user.getId());
        return r2dbcRepository.save(userMapper.toEntity(user))
                .doOnNext(entity -> entity.setNotNew())
                .map(userMapper::toModel);
    }

    @Override
    public Mono<User> findById(String id) {
        log.debug("Finding user by ID: {}", id);
        return r2dbcRepository.findById(id)
                .doOnNext(entity -> entity.setNotNew())
                .map(userMapper::toModel);
    }

    @Override
    public Mono<User> findByDocumentNumber(String documentNumber) {
        log.debug("Finding user by document: {}", documentNumber);
        return r2dbcRepository.findByDocumentNumber(documentNumber)
                .doOnNext(entity -> entity.setNotNew())
                .map(userMapper::toModel);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return r2dbcRepository.findByEmail(email)
                .doOnNext(entity -> entity.setNotNew())
                .map(userMapper::toModel);
    }

    @Override
    public Flux<User> findAll() {
        log.debug("Finding all users");
        return r2dbcRepository.findAll()
                .doOnNext(entity -> entity.setNotNew())
                .map(userMapper::toModel);
    }

    @Override
    public Flux<User> findByRecordStatus(String recordStatus) {
        log.debug("Finding users by status: {}", recordStatus);
        return r2dbcRepository.findByRecordStatus(recordStatus)
                .doOnNext(entity -> entity.setNotNew())
                .map(userMapper::toModel);
    }

    @Override
    public Flux<User> findByOrganizationId(String organizationId) {
        log.debug("Finding users by organization: {}", organizationId);
        return r2dbcRepository.findByOrganizationId(organizationId)
                .doOnNext(entity -> entity.setNotNew())
                .map(userMapper::toModel);
    }

    @Override
    public Flux<User> findByOrganizationIdAndRecordStatus(String organizationId, String recordStatus) {
        log.debug("Finding users by organization {} and status {}", organizationId, recordStatus);
        return r2dbcRepository.findByOrganizationIdAndRecordStatus(organizationId, recordStatus)
                .doOnNext(entity -> entity.setNotNew())
                .map(userMapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByDocumentNumber(String documentNumber) {
        log.debug("Checking if exists by document: {}", documentNumber);
        return r2dbcRepository.existsByDocumentNumber(documentNumber);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        log.warn("Physically deleting user: {}", id);
        return r2dbcRepository.deleteById(id);
    }
}
