package pe.edu.vallegrande.vgmsorganizations.application.usescases.organization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base.BusinessRuleException;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.OrganizationNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Organization;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.organization.IRestoreOrganizationUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization.IOrganizationEventPublisher;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization.IOrganizationRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreOrganizationUseCaseImpl implements IRestoreOrganizationUseCase {

    private final IOrganizationRepository organizationRepository;
    private final IOrganizationEventPublisher eventPublisher;

    @Override
    public Mono<Organization> execute(String id, String restoredBy) {
        log.info("Restoring organization: {}", id);

        return organizationRepository.findById(id)
            .switchIfEmpty(Mono.error(new OrganizationNotFoundException(id)))
            .flatMap(org -> {
                if (org.isActive()) {
                    return Mono.error(new BusinessRuleException("Organization is already active"));
                }

                Organization restored = org.restore(restoredBy);
                return organizationRepository.update(restored)
                    .flatMap(saved -> publishRestoreEvent(saved, restoredBy));
            })
            .doOnSuccess(o -> log.info("Organization restored: {}", o.getId()))
            .doOnError(e -> log.error("Error restoring organization: {}", e.getMessage()));
    }

    private Mono<Organization> publishRestoreEvent(Organization org, String restoredBy) {
        return eventPublisher.publishOrganizationRestored(org.getId(), restoredBy)
            .thenReturn(org)
            .onErrorResume(e -> {
                log.warn("Failed to publish restore event: {}", e.getMessage());
                return Mono.just(org);
            });
    }
}