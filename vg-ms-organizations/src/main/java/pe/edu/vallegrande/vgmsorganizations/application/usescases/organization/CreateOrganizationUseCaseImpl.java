package pe.edu.vallegrande.vgmsorganizations.application.usescases.organization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.DuplicateOrganizationException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Organization;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.organization.ICreateOrganizationUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization.IImageStoragePort;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization.IOrganizationEventPublisher;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization.IOrganizationRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrganizationUseCaseImpl implements ICreateOrganizationUseCase {

    private static final String LOGO_FOLDER = "jass/logos";

    private final IOrganizationRepository organizationRepository;
    private final IOrganizationEventPublisher eventPublisher;
    private final IImageStoragePort imageStoragePort;

    @Override
    public Mono<Organization> execute(Organization organization, String createdBy) {
        log.info("Creating organization: {}", organization.getOrganizationName());

        return organizationRepository.existsByOrganizationName(organization.getOrganizationName())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateOrganizationException(organization.getOrganizationName()));
                    }

                    organization.validateContact();
                    return uploadLogoIfPresent(organization.getLogoUrl());
                })
                .map(logoUrl -> organization.toBuilder()
                        .id(UUID.randomUUID().toString())
                        .logoUrl(logoUrl)
                        .recordStatus(RecordStatus.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .createdBy(createdBy)
                        .updatedAt(LocalDateTime.now())
                        .updatedBy(createdBy)
                        .build())
                .flatMap(organizationRepository::save)
                .flatMap(saved -> publishCreateEvent(saved, createdBy))
                .doOnSuccess(o -> log.info("Organization created: {}", o.getId()))
                .doOnError(e -> log.error("Error creating organization: {}", e.getMessage()));
    }

    private Mono<String> uploadLogoIfPresent(String logoUrl) {
        if (logoUrl == null || logoUrl.isBlank()) {
            return Mono.just("");
        }
        return imageStoragePort.uploadFromUrl(logoUrl, LOGO_FOLDER);
    }

    private Mono<Organization> publishCreateEvent(Organization org, String createdBy) {
        return eventPublisher.publishOrganizationCreated(org, createdBy)
                .thenReturn(org)
                .onErrorResume(e -> {
                    log.warn("Failed to publish create event: {}", e.getMessage());
                    return Mono.just(org);
                });
    }
}
