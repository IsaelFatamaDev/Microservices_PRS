package pe.edu.vallegrande.vgmsorganizations.application.usescases.organization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base.BussinessRuleException;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.OrganizationNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Organization;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.organization.IDeleteOrganizationUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization.IOrganizationEventPublisher;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization.IOrganizationRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteOrganizationUseCaseImpl implements IDeleteOrganizationUseCase {
    private final IOrganizationRepository organizationRepository;
    private final IOrganizationEventPublisher eventPublisher;

    @Override
    public Mono<Organization> execute(String id, String deletedBy, String reason) {
        log.info("Deleting organization: {} - Reason: {}", id, reason);
        return organizationRepository.findById(id)
            .switchIfEmpty(Mono.error(new OrganizationNotFoundException(id)))
            .flatMap(org -> {
                if (org.isInactive()) {
                    return Mono.error(new BussinessRuleException("Organization is already inactive"));
                }

                Organization deleted = org.markAsDeleted(deletedBy);
                return organizationRepository.update(deleted)
                    .flatMap(saved -> publishDeleteEvent(saved.getId(), reason, deletedBy));
            })
            .doOnSuccess(o -> log.info("Organization deleted: {}", o.getId()))
            .doOnError(e -> log.error("Error deleting organization: {}", e.getMessage()));
    }

    private Mono<Organization> publishDeleteEvent(String orgId, String reason, String deletedBy) {
        return eventPublisher.publishOrganizationDeleted(orgId, reason, deletedBy)
            .then(organizationRepository.findById(orgId))
            .onErrorResume(e -> {
                log.warn("Failed to publish delete event: {}", e.getMessage());
                return organizationRepository.findById(orgId);
            });
    }
}
