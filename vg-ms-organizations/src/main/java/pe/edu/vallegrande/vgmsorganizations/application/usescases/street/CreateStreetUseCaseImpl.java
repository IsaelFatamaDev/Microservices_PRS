package pe.edu.vallegrande.vgmsorganizations.application.usescases.street;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base.BusinessRuleException;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.ZoneNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Street;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.street.ICreateStreetUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.street.IStreetEventPublisher;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.street.IStreetRepository;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.zone.IZoneRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateStreetUseCaseImpl implements ICreateStreetUseCase {

    private final IStreetRepository streetRepository;
    private final IZoneRepository zoneRepository;
    private final IStreetEventPublisher eventPublisher;

    @Override
    public Mono<Street> execute(Street street, String createdBy) {
        log.info("Creating street: {} in zone: {}", street.getStreetName(), street.getZoneId());

        return zoneRepository.findById(street.getZoneId())
            .switchIfEmpty(Mono.error(new ZoneNotFoundException(street.getZoneId())))
            .flatMap(zone -> {
                if (zone.isInactive()) {
                    return Mono.error(new BusinessRuleException("Cannot create a street in an inactive zone"));
                }
                return streetRepository.existsByStreetNameAndZoneId(street.getStreetName(), street.getZoneId());
            })
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new BusinessRuleException(
                        String.format("Street '%s' already exists in this zone", street.getStreetName())
                    ));
                }

                Street newStreet = street.toBuilder()
                    .id(UUID.randomUUID().toString())
                    .recordStatus(RecordStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .createdBy(createdBy)
                    .updatedAt(LocalDateTime.now())
                    .updatedBy(createdBy)
                    .build();

                return streetRepository.save(newStreet);
            })
            .flatMap(saved -> eventPublisher.publishStreetCreated(saved, createdBy)
                .thenReturn(saved)
                .onErrorResume(e -> {
                    log.warn("Failed to publish street create event: {}", e.getMessage());
                    return Mono.just(saved);
                })
            )
            .doOnSuccess(s -> log.info("Street created: {}", s.getId()))
            .doOnError(e -> log.error("Error creating street: {}", e.getMessage()));
    }
}