package pe.edu.vallegrande.vgmsorganizations.application.usescases.organization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.OrganizationNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Organization;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.organization.IUpdateOrganizationUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization.IImageStoragePort;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization.IOrganizationEventPublisher;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization.IOrganizationRepository;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateOrganizationUseCaseImpl implements IUpdateOrganizationUseCase {

    private static final String LOGO_FOLDER = "jass/logos";

    private final IOrganizationEventPublisher eventPublisher;
    private final IOrganizationRepository organizationRepository;
    private final IImageStoragePort imageStoragePort;

    @Override
    public Mono<Organization> execute(String id, Organization changes, String updatedBy) {
        log.info("Updating organization: {}", id);
        return organizationRepository.findById(id)
                .switchIfEmpty(Mono.error(new OrganizationNotFoundException(id)))
                .flatMap(existing -> {
                    Map<String, Object> changedFields = detectChanges(existing, changes);
                    if (changedFields.isEmpty()) {
                        log.info("No changes detected for organization: {}", id);
                        return Mono.just(existing);
                    }
                    return uploadLogoIfChanged(existing, changes)
                            .map(uploadedChanges -> existing.updateWith(uploadedChanges, updatedBy))
                            .flatMap(organizationRepository::update)
                            .flatMap(saved -> publishUpdateEvent(saved, changedFields, updatedBy));
                })
                .doOnSuccess(o -> log.info("Organization updated: {}", o.getId()))
                .doOnError(e -> log.error("Error updating organization: {}", e.getMessage()));
    }

    private Mono<Organization> uploadLogoIfChanged(Organization existing, Organization changes) {
        String newLogoUrl = changes.getLogoUrl();
        if (newLogoUrl == null || newLogoUrl.isBlank() || newLogoUrl.equals(existing.getLogoUrl())) {
            return Mono.just(changes);
        }
        return imageStoragePort.uploadFromUrl(newLogoUrl, LOGO_FOLDER)
                .map(cloudinaryUrl -> changes.toBuilder().logoUrl(cloudinaryUrl).build())
                .defaultIfEmpty(changes);
    }

    private Map<String, Object> detectChanges(Organization existing, Organization changes) {
        Map<String, Object> changedFields = new HashMap<>();

        if (changes.getOrganizationName() != null
                && !changes.getOrganizationName().equals(existing.getOrganizationName())) {
            changedFields.put("organizationName", changes.getOrganizationName());
        }
        if (changes.getDistrict() != null && !changes.getDistrict().equals(existing.getDistrict())) {
            changedFields.put("district", changes.getDistrict());
        }
        if (changes.getProvince() != null && !changes.getProvince().equals(existing.getProvince())) {
            changedFields.put("province", changes.getProvince());
        }
        if (changes.getDepartment() != null && !changes.getDepartment().equals(existing.getDepartment())) {
            changedFields.put("department", changes.getDepartment());
        }
        if (changes.getAddress() != null && !changes.getAddress().equals(existing.getAddress())) {
            changedFields.put("address", changes.getAddress());
        }
        if (changes.getPhone() != null && !changes.getPhone().equals(existing.getPhone())) {
            changedFields.put("phone", changes.getPhone());
        }
        if (changes.getEmail() != null && !changes.getEmail().equals(existing.getEmail())) {
            changedFields.put("email", changes.getEmail());
        }
        if (changes.getLogoUrl() != null && !changes.getLogoUrl().equals(existing.getLogoUrl())) {
            changedFields.put("logoUrl", changes.getLogoUrl());
        }

        return changedFields;
    }

    private Mono<Organization> publishUpdateEvent(Organization org, Map<String, Object> changedFields,
            String updatedBy) {
        return eventPublisher.publishOrganizationUpdated(org, changedFields, updatedBy)
                .thenReturn(org)
                .onErrorResume(e -> {
                    log.warn("Failed to publish update event: {}", e.getMessage());
                    return Mono.just(org);
                });
    }
}
