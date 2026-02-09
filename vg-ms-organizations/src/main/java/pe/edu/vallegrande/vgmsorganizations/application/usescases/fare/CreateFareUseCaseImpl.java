package pe.edu.vallegrande.vgmsorganizations.application.usescases.fare;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.OrganizationNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Fare;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.fare.ICreateFareUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.fare.IFareEventPublisher;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.fare.IFareRepository;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization.IOrganizationRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateFareUseCaseImpl implements ICreateFareUseCase {

    private final IFareRepository fareRepository;
    private final IOrganizationRepository organizationRepository;
    private final IFareEventPublisher eventPublisher;

    @Override
    public Mono<Fare> execute(Fare fare, String createdBy) {
        log.info("Creating fare: {} for organization: {}", fare.getFareType(), fare.getOrganizationId());

        return organizationRepository.findById(fare.getOrganizationId())
            .switchIfEmpty(Mono.error(new OrganizationNotFoundException(fare.getOrganizationId())))
            .flatMap(org -> {
                fare.validateAmount();

                return fareRepository.findActiveByOrganizationAndType(fare.getOrganizationId(), fare.getFareType())
                    .flatMap(oldFare -> fareRepository.update(oldFare.markAsDeleted(createdBy)))
                    .then(Mono.defer(() -> {
                        Fare newFare = fare.toBuilder()
                            .id(UUID.randomUUID().toString())
                            .recordStatus(RecordStatus.ACTIVE)
                            .createdAt(LocalDateTime.now())
                            .createdBy(createdBy)
                            .updatedAt(LocalDateTime.now())
                            .updatedBy(createdBy)
                            .build();

                        return fareRepository.save(newFare);
                    }));
            })
            .flatMap(saved -> eventPublisher.publishFareCreated(saved, createdBy)
                .thenReturn(saved)
                .onErrorResume(e -> Mono.just(saved))
            );
    }
}