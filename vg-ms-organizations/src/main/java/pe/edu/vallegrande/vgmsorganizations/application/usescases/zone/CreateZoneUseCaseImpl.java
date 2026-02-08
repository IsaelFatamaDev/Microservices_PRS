package pe.edu.vallegrande.vgmsorganizations.application.usescases.zone;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base.BussinessRuleException;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.OrganizationNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Zone;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.zone.ICreateZoneUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization.IOrganizationRepository;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.zone.IZoneEventPublisher;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.zone.IZoneRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateZoneUseCaseImpl implements ICreateZoneUseCase {

    private final IOrganizationRepository organizationRepository;
    private final IZoneRepository zoneRepository;
    private final IZoneEventPublisher eventPublisher;

    @Override
    public Mono<Zone> execute(Zone zone, String createdBy) {
        log.info("Creating zone: {} for organization: {}", zone.getZoneName(), zone.getOrganizationId());

        return organizationRepository.findById(zone.getOrganizationId())
            .switchIfEmpty(Mono.error(new OrganizationNotFoundException(zone.getOrganizationId())))
            .flatMap(org -> {
                if (org.isInactive()) {
                    return Mono.error(new BussinessRuleException("Cannot create a zone in an inactive organization"));
                }
                return zoneRepository.existsByZoneNameAndOrganizationId(zone.getZoneName(), zone.getOrganizationId());
            })
            .flatMap(exits -> {
                if (exits) {
                    return Mono.error(new BussinessRuleException(
                        String.format("Zone '%s' already exists for organization '%s'", zone.getZoneName(), zone.getOrganizationId())
                    ));
                }
                Zone newZone = zone.toBuilder()
                    .id(UUID.randomUUID().toString())
                    .recordStatus(RecordStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .createdBy(createdBy)
                    .updatedAt(LocalDateTime.now())
                    .updatedBy(createdBy)
                    .build();

                return zoneRepository.save(newZone);
            })
            .flatMap(saved -> eventPublisher.publishZoneCreated(saved, createdBy)
                .thenReturn(saved)
                .onErrorResume(e -> {
                        log.warn("Failed to publish zone create event: {}", e.getMessage());
                        return Mono.just(saved);
                    }
                )
            )
            .doOnSuccess(z -> log.info("Zone created: {}", z.getId()))
            .doOnError(e -> log.error("Error creating zone: {}", e.getMessage()));

    }
}
