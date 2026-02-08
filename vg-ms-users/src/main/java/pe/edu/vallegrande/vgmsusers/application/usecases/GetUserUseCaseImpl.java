package pe.edu.vallegrande.vgmsusers.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsusers.domain.exceptions.UserNotFoundException;
import pe.edu.vallegrande.vgmsusers.domain.models.User;
import pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsusers.domain.ports.in.IGetUserUseCase;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.IUserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserUseCaseImpl implements IGetUserUseCase {

    private final IUserRepository userRepository;

    @Override
    public Mono<User> findById(String id) {
        log.debug("Finding user by ID: {}", id);
        return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
            .doOnSuccess(u -> log.debug("User found: {}", u.getId()));
    }

    @Override
    public Mono<User> findByDocumentNumber(String documentNumber) {
        log.debug("Finding user by document number: {}", documentNumber);
        return userRepository.findByDocumentNumber(documentNumber)
            .switchIfEmpty(Mono.error(UserNotFoundException.byDocument(documentNumber)));
    }

    @Override
    public Mono<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
            .switchIfEmpty(Mono.error(UserNotFoundException.byEmail(email)));
    }

    @Override
    public Flux<User> findAllActive() {
        log.debug("Finding all active users");
        return userRepository.findByRecordStatus(RecordStatus.ACTIVE.name());
    }

    @Override
    public Flux<User> findAll() {
        log.debug(("Finding all users"));
        return userRepository.findAll();
    }

    @Override
    public Flux<User> findByOrganizationId(String organizationId) {
        log.debug("Finding users by organization: {}", organizationId);
        return userRepository.findByOrganizationId(organizationId);
    }

    @Override
    public Flux<User> findActiveByOrganizationId(String organizationId) {
        log.debug("Finding active users by organization: {}", organizationId);
        return userRepository.findByOrganizationIdAndRecordStatus(organizationId, RecordStatus.ACTIVE.name());
    }

    @Override
    public Mono<Boolean> existsByDocumentNumber(String documentNumber) {
        return userRepository.existsByDocumentNumber(documentNumber);
    }
}
